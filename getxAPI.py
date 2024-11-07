import json
import requests
from requests.auth import HTTPBasicAuth


read_endpoint = 'https://ll.inokufu.com/api/v2/statement'

response = requests.get(
    read_endpoint,
    auth=HTTPBasicAuth('a65e043f9e1b3d0b02d82da5d2ea0ebc473d712e', '21adf57fd1685b7c90c83ce5fc58cc794dfd98ad'),
    headers={'Content-Type': 'application/json'}
)

if response.status_code == 200:
    data = response.json()
    
    with open('xapi_PLRS.json', 'w') as json_file:
        json.dump(data, json_file, indent=4)
    
    print("xAPI statements retrieved and saved to xapi_statements.json.")
else:
    print(f"Failed to retrieve statements: {response.status_code} - {response.text}")