import json
import pandas as pd
import great_expectations as ge
from datetime import datetime, timedelta, timezone

vla_path = r"C:\\Egyetem\\vla\\data_veracity\Backend\\app\\VLA.json"
data_path = r"C:\\Egyetem\\vla\\data_veracity\\xapi_WALRUC.json"
results = []

with open(vla_path, "r",  encoding="utf-8") as f:
    vla = json.load(f)
with open(data_path, "r",  encoding="utf-8") as f:
    data_time = json.load(f)
data = pd.read_json(data_path)

df_ge = ge.dataset.PandasDataset(data)
validation_results_nottime = []

def apply_validate_timestamp(validation_rules, data_records):
    for rule in validation_rules:
        aspect = rule["aspect"]

        if aspect == "syntax":
            continue

        evaluation = rule["evaluation"]
        description = rule.get("description", "No description provided")

        if aspect == "Sequiental":
            timestamps = []
            for record in data_records:
                if isinstance(record, dict) and "statement" in record:
                    timestamp = record["statement"].get("timestamp")
                    if timestamp:
                        timestamps.append(datetime.fromisoformat(timestamp[:-1]))
            
            if timestamps == sorted(timestamps):
                results.append((aspect, True, f"{description}: Timestamps are sequential."))
            else:
                results.append((aspect, False, f"{description}: Timestamps are not sequential."))

        elif aspect == "Timestamp_Within_Range":
            target = evaluation["target"]
            unit = evaluation["unit"]
            measurement = evaluation["measurement"]
            range_end = datetime.utcnow()
            if unit == "Week":
                range_start = range_end - timedelta(weeks=measurement)
            elif unit == "Day":
                range_start = range_end - timedelta(days=measurement)
            elif unit == "Mount":
                range_start = range_end - timedelta(days=30 * measurement)
            else:
                results.append({aspect, False, f"{description}: Some timestamps are out of range."})
                continue

            timestamps = []
            for record in data_records:
                if isinstance(record, dict) and "statement" in record:
                    timestamp = record["statement"].get(target.split(".")[-1])
                    if timestamp:
                        timestamps.append(datetime.fromisoformat(timestamp[:-1]))
            
            if all(range_start <= ts <= range_end for ts in timestamps):
                results.append({aspect, True, f"{description}: All timestamps are within range."})
            else:
                results.append({aspect, False,f"{description}: Some timestamps are out of range."})
    return results

def apply_validation(objective):
    aspect = objective["aspect"]
    evaluation = objective["evaluation"]
    description = objective["description"]

    try:
        if aspect == "Record_Count":
            min_count = evaluation.get("min")
            max_count = evaluation.get("max")
            exact = evaluation.get("exact")
            if min_count is not None and max_count is not None:
                result = df_ge.expect_table_row_count_to_be_between(min_value=min_count, max_value=max_count)
            elif min_count is not None:
                result = df_ge.expect_table_row_count_to_be_between(min_value=min_count, max_value=None)
            elif max_count is not None:
                result = df_ge.expect_table_row_count_to_be_between(min_value=0, max_value=max_count)
            elif exact is not None:
                result = df_ge.expect_table_row_count_to_be_between(min_value=exact, max_value=exact)


        elif aspect == "String_value":
            target = evaluation["target"]
            expected_value = evaluation["unit"]
            result = df_ge.expect_column_values_to_be_in_set(column=target, value_set=[expected_value])

        else:
            raise ValueError(f"Unknown aspect: {aspect}")

        success = result["success"]
        return {"description": description, "aspect": aspect, "success": success, "error": ""}

    except Exception as e:
        return {"description": description, "aspect": aspect, "success": False, "error": str(e)}

for objective in vla["objectives"]:
    if objective["aspect"] != "syntax" and objective["aspect"] != "Sequiental"  and objective["aspect"] != "Timestamp_Within_Range" : 
        result = apply_validation(objective)
        validation_results_nottime.append(result)

output_path = r"C:\\Egyetem\\vla\\data_veracity\\value_validation_result.json"
validation_data = vla
validation_rules = validation_data.get("objectives", [])
data_records = data_time
validation = True
validation_results = apply_validate_timestamp(validation_rules, data_records)
validation_report = df_ge.validate()
output_path = "validation_results.json"
with open(output_path, "w") as f:
    json.dump(validation_report.to_json_dict(), f, indent=4)
final_result = []
for aspect, is_valid, message in validation_results:
    if(is_valid):
        status = "PASSED"
    
    else:
        status = "FAILED"
        validation = False
    final_result.append(f"Validation {aspect}: {status} - {message}")
for result_nottime in validation_results_nottime:
    if not result_nottime["success"]:
        validation = False
        final_result.append(f"Validation {result_nottime['aspect']}: FAILED - {result_nottime['description']}")
    else:
        final_result.append(f"Validation {result_nottime['aspect']}: PASSED - {result_nottime['description']}")
with open(output_file_path, 'w') as output_file:
    json.dump(final_result, output_file, indent=4)

if(validation):
    print("valid")
else:
    print("invalid")