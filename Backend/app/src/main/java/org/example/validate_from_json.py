import great_expectations as gx
import great_expectations.expectations as gxe
import json
import sys
import psycopg2
import time
import sys


def validate_from_json(json_file):
    results = {}
    with open(json_file, "r") as file:
        validation_data = json.load(file)

    context = gx.get_context(mode="file")
    datasource = context._datasources.get(validation_data["datasource"])
    if datasource is None:
        datasource = context.data_sources.add_sql(
            name=validation_data["datasource"],
            connection_string=validation_data["connection_string"]
        )

    table_name = validation_data["table_name"]
    asset_name = "house"
    if asset_name in [asset.name for asset in datasource.assets]:
        data_asset = datasource.get_asset(asset_name)
    else:
        data_asset = datasource.add_table_asset(name=asset_name, table_name=table_name)

    unique_batch_name = f"batch_definition_{int(time.time())}"
    batch_definition = data_asset.add_batch_definition_whole_table(unique_batch_name)

    batch = batch_definition.get_batch()

    for expectation in validation_data["expectations"]:
        column = expectation["columnName"]
        value = expectation["value"]
        valtype = expectation["valtype"]
        result = {"column": column, "status": "Not Implemented"}
        if valtype == "Cannot be empty":
            exp = gxe.ExpectColumnValuesToNotBeNull(column=column)
            result["status"] = batch.validate(exp).success
        elif valtype == "Cannot be true":
            exp = gxe.ExpectColumnValuesToBeFalse(column=column)
            result["status"] = batch.validate(exp).success
        elif valtype == "Min":
            exp = gxe.ExpectColumnValuesToBeBetween(column=column,min_value=value, max_value=None, strict_min=False)
            result["status"] = batch.validate(exp).success

        results[column] = result

    with open("validation_results.json", "w") as result_file:
        json.dump(results, result_file)

    print("Validation completed. Results saved to validation_results.json")


if __name__ == "__main__":
    #json_file = sys.argv[1]
    validate_from_json("C:\\Egyetem\\szakdoga\\szakdogaa\\app\\expectations.json")
