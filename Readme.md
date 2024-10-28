# Definition, Validation and Evaluation of Data Veracity Level Aggrements 

### Data structure format for validation aggrements:
``` bash
Database_config:
    - type: postgresql
    - host: localhost
    - port: 5432
    - username: house
    - password:asd
    - database: house_db
Data_types: 
    - name: Name
      type: String
      expectation:
        - nullable: false
        - default: ""
    - name: Username
      type: String
      expectation:
        - nullable: false
        - default: ""
        - type: "regex"
          condition: "^[a-zA-Z0-9_]{8,16}$"
    - name: People_Count
      type: Integer
      expectation: 
        - nullable: false
        - default: 1
        - type: "range"
          min: 1
          max: 6
        - type: "Min"
          min: 10
    - name: Has_garage
      type: boolean
      expectation:
        - nullable: true
        - default: false
        - type: "bool constraint"
          condition: "Cannot be true"
    - name: Sex
      type: String
      expectation:
        - nullable: false
        - default: null
        - type: "enum"
          condition: "M,F"
Special_expectations:
    - name: Username
    expectation:
      - type: "cant contain"
        value: "Name cant be in Username"

```

The Database config part only needed for this demo project, not for the production product.
### Default data_structure if the partners didn't define it:
``` bash
name: default name
type: default type
nullable: false
default: null
```
### The default type constraints:

- Integer

    - range

    - min

    - max

- String

    - Contains substring

    - regex

    - enum

- Boolean
    
    - Cant/Must be true

    - Cant/Must be false
 
