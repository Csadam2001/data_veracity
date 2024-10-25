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
Data_structure: 
    - name: Name
      type: String
      nullable: false
      default: ""
    - name: Username
      tpye: String
      nullabel: false
      default: ""
    - name: People_Count
      type: Integer
      nullable: false
      default: 1
    - name: Has_garage
      type: boolean
      nullable: true
      default: false
    - name: Sex
      type: String
      nullabel: false
      default: null
Special_expectations:
    - name: People_count
      expectation: 
        - type: "range"
          min: 1
          max: 6
        - type: "Min"
          min: 10
    - name: Has_pet
      expectation:
        - type: "bool constraint"
          condition: "Cannot be true"
    - name: Sex
      expectation:
        - type: "enum"
          condition: "M,F"
    - name: Username
      expectation:
        type: "regex"
          condition: "^[a-zA-Z0-9_]{8,16}$"

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
 
