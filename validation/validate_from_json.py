import json
import great_expectations as gx

def extract_terminal_paths(schema, parent_path=""):
    paths = []
    if "properties" in schema:
        for key, value in schema["properties"].items():
            current_path = f"{parent_path}.{key}" if parent_path else key
            if "type" in value and "properties" not in value:
                paths.append((current_path, value["type"]))
            elif "properties" in value:
                paths.extend(extract_terminal_paths(value, current_path))
    return paths

def validate_terminal_paths(record, terminal_paths_and_types):
    validation_results = []

    def get_value_from_path(data, path):
        keys = path.split(".")
        for key in keys:
            if isinstance(data, dict) and key in data:
                data = data[key]
            else:
                return None
        return data

    for path, expected_type in terminal_paths_and_types:
        actual_value = get_value_from_path(record, path)
        actual_type = type(actual_value).__name__ if actual_value is not None else None

        python_to_json_types = {
            "str": "string",
            "int": "integer",
            "bool": "boolean",
            "list": "array",
        }
        actual_type_json = python_to_json_types.get(actual_type, "unknown")

        if actual_value is None:
            validation_results.append((path, "Missing", expected_type))
        elif actual_type_json != expected_type:
            validation_results.append((path, "Type Mismatch", f"Expected {expected_type}, Got {actual_type_json}"))
        else:
            validation_results.append((path, "Valid", expected_type))
    
    return validation_results

validation_json_path = r"C:\\Egyetem\\szakdoga\\szakdoga\\szakdoga\\Backend\\app\\VLA.json"
target_json_path = r"C:\Egyetem\szakdoga\szakdoga\szakdoga\xapi_WALRUC.json"

with open(validation_json_path, "r") as val_file:
    validation_json = json.load(val_file)

with open(target_json_path, "r") as tgt_file:
    target_json = json.load(tgt_file)

schema_paths_and_types = extract_terminal_paths(validation_json["objectives"][0]["evaluation"]["schema"])

if not isinstance(target_json, list):
    target_json = [target_json]

all_results = []

for idx, record in enumerate(target_json):
    validation_results = validate_terminal_paths(record, schema_paths_and_types)
    all_results.append((idx, validation_results))

output_file_path = r"C:\\Egyetem\\szakdoga\\szakdoga\\szakdoga\\syntax_validation_result.json"

output_data = []
all_paths_valid = True 

for record_idx, results in all_results:
    record_results = {"record_idx": record_idx + 1, "validations": []}
    
    for path, status, details in results:
        record_results["validations"].append({
            "path": path,
            "status": status,
            "details": details
        })
        if status != "Valid":
            all_paths_valid = False 
    
    output_data.append(record_results)

with open(output_file_path, 'w') as output_file:
    json.dump(output_data, output_file, indent=4)

if all_paths_valid:
    print("valid")
else:
    print("invalid")
