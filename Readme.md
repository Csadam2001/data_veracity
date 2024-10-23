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
    - name: People_Count
      type: Integer
      nullable: false
      default: 1
    - name: Has_garage
      type: boolean
      nullable: true
      default: false
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

```
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

- Boolean
    
    - Cant/Must be true

    - Cant/Must be false
 
