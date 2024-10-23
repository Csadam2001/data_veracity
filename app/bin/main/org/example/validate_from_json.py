import great_expectations as gx
import great_expectations.expectations as gxe
import json
import sys

def validate_from_json(json_file):
    with open(json_file, "r") as file:
        validation_data = json.load(file)
    
    context = gx.get_context(mode="file")
    datasource = context._datasources.get(validation_data["datasource"])
    
    if datasource is None:
        datasource = context.data_sources.add_postgres(
            validation_data["datasource"],
            connection_string=validation_data["connection_string"]
        )
    
    table_name = validation_data["table_name"]
    data_asset = datasource.get_asset(table_name)
    
    if data_asset is None:
        data_asset = datasource.add_table_asset(name=table_name, table_name=table_name)
    
    batch_definition = data_asset.get_batch_definition("batch_definition")
    
    if batch_definition is None:
        batch_definition = data_asset.add_batch_definition_whole_table("batch_definition")
    
    batch = batch_definition.get_batch()
    
    # Loop through the expectations defined in the JSON file
    for expectation in validation_data["expectations"]:
        column = expectation["column"]
        if expectation["type"] == "ExpectColumnMaxToBeBetween":
            max_value = expectation["max_value"]
            exp = gxe.ExpectColumnMaxToBeBetween(column=column, max_value=max_value)
            result = batch.validate(exp)
            print(f"Validation result for column {column}: {result}")
        # Add other expectation types here as needed

if __name__ == "__main__":
    json_file = sys.argv[1]  # Take the JSON file as a command-line argument
    validate_from_json(json_file)
