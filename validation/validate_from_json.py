import json

def extract_schema(vla_schema, parent_key=""):
    """Recursively extracts the schema paths and types from the VLA."""
    paths = {}
    for key, value in vla_schema.get("properties", {}).items():
        current_path = f"{parent_key}.{key}" if parent_key else key
        if value["type"] == "object":
            paths.update(extract_schema(value, current_path))
        elif value["type"] == "array":
            item_type = value.get("properties", {}).get("items", {}).get("type", "string")
            paths[current_path] = f"array<{item_type}>"
        else:
            paths[current_path] = value["type"]
    return paths

def validate_type(value, expected_type):
    """Validates the type of a value against the expected type."""
    type_mapping = {
        "string": str,
        "number": (int, float),
        "boolean": bool,
        "object": dict,
        "array": list
    }
    if "array" in expected_type:
        if not isinstance(value, list):
            return False, f"Expected array but found {type(value).__name__}"
        item_type = expected_type.split("<")[1][:-1]
        for item in value:
            is_valid, _ = validate_type(item, item_type)
            if not is_valid:
                return False, f"Array contains invalid item of type {type(item).__name__}"
        return True, None
    elif expected_type in type_mapping:
        if not isinstance(value, type_mapping[expected_type]):
            return False, f"Expected {expected_type} but found {type(value).__name__}"
        return True, None
    else:
        return False, f"{type(value).__name__} expected type: {expected_type}"

def validate_json(record, schema):
    """Validates a JSON record against the extracted schema."""
    errors = []
    for key, expected_type in schema.items():
        keys = key.split(".")
        current_value = record
        try:
            for k in keys:
                if k == "items":
                    continue
                current_value = current_value[k]
        except (KeyError, TypeError):
            errors.append(f"Missing or invalid path '{key}'")
            continue
        is_valid, error = validate_type(current_value, expected_type)
        if not is_valid:
            errors.append(f"Path '{key}': {error}")
    return errors

def main():
    vla_path = r"C:\\Egyetem\\vla\\data_veracity\Backend\\app\\VLA.json"
    records_path = r"C:\\Egyetem\\vla\\data_veracity\\xapi_WALRUC.json"
    output_path = r"C:\\Egyetem\\vla\\data_veracity\\syntax_validation_result.json"
    
    with open(vla_path, "r", encoding="utf-8") as vla_file:
        vla = json.load(vla_file)
    
    schema = extract_schema(vla["objectives"][0]["evaluation"]["schema"])
    
    with open(records_path, "r", encoding="utf-8") as records_file:
        records = json.load(records_file)
    
    all_errors = []
    for i, record in enumerate(records):
        errors = validate_json(record, schema)
        if errors:
            all_errors.append({"record_index": i, "errors": errors})
    
    result = {
        "status": "valid" if not all_errors else "invalid",
        "errors": all_errors
    }
    with open(output_path, "w", encoding="utf-8") as output_file:
        json.dump(result, output_file, indent=4)
    
    if all_errors:
        print("invalid")
    else:
        print("valid")

if __name__ == "__main__":
    main()
