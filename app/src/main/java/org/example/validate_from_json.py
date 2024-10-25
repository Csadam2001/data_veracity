import great_expectations as gx
import great_expectations.expectations as gxe
import json
import sys
import psycopg2


def validate_from_json(json_file):
    results = {}
    with open(json_file, "r") as file:
        validation_data = json.load(file)

    context = gx.get_context(mode="file")

    # Explicitly add the datasource if it doesn't exist
    datasource = context.data_sources.get(validation_data["datasource"])

    if datasource is None:
        datasource = context.sources.add_sql(
            name=validation_data["datasource"],
            connection_string=validation_data["connection_string"]
        )

    table_name = validation_data["table_name"]

    # Debug: List available assets in the datasource
    #print("Available assets in datasource:", datasource.list_assets())

    data_asset = datasource.get_asset("houses")
    #print(f"Table {table_name} not found, adding it explicitly.")
    #data_asset = datasource.add_table_asset(name=table_name, table_name=table_name, schema_name="public")

    # Get batch definition
    #batch_definition = data_asset.get_batch_definition("batch_definition")

    batch_definition = data_asset.add_batch_definition_whole_table("batch_definition")

    batch = batch_definition.get_batch()

    for expectation in validation_data["expectations"]:
        column = expectation["columnName"]
        value = expectation["value"]
        result = {"column": column, "status": "Not Implemented"}  # Placeholder for actual validation result
        if value == "Cannot be empty":
            exp = gxe.ExpectColumnValuesToNotBeNull(column=column)
            result["status"] = batch.validate(exp).success
        elif value == "Cannot be true":
            exp = gxe.ExpectColumnValuesToBeFalse(column=column)
            result["status"] = batch.validate(exp).success
        # Add more conditions for other validation types

        results[column] = result

    # Write results to a file
    with open("validation_results.json", "w") as result_file:
        json.dump(results, result_file)

    print("Validation completed. Results saved to validation_results.json")


if __name__ == "__main__":
    json_file = sys.argv[1]
    validate_from_json(json_file)
