<template>
  <div class="header">
    <img style=" height: 130px; margin-right: 30px;" src="../assets/images.png" alt="Header Image" />
    <label class="header-label">Veracity Level Agreement Creating Site</label>
    <label class="header_role">Role: Producer</label>
    <img style=" padding-left: 20px; height: 100px; background-color: white;" src="../assets/LOGO-PROMETHEUS-X.png" alt="Header Image" />
  </div>
  <hr />
  <div v-if="!jsonloaded" style="margin-top: 40px; margin-left: 20px;">
    <input
  type="file"
  id="upload"
  class="file-input"
  @change="handleFileUpload"
/>
<label for="upload" class="custom-file-label">Upload File</label>
  </div>
  
  <div v-if="moreExpectations && jsonloaded">
    <div class="temp">
      <div class="Json">
        <h1 style="font-size: 40px;">JSON Structure</h1>
        <button v-if="!jsonended" @click="toggleJsonStructure">
          {{ showJson ? 'Hide JSON Structure' : 'Load JSON Structure' }}
        </button>
        <div class="struct" v-if="showJson && !jsonended && jsonStructure && Object.keys(jsonStructure).length">
          <JsonTree ref="jsonTree" :node="jsonStructure" :path="[]" @keySelected="handleKeySelection"
            @updateJson="updateJsonStructure" />
        </div>
        <button v-if="showJson && !jsonended" @click="savestruct">Save Struct</button>
        <label style="font-size: 23px;" v-if="jsonended == true"> Json structure saved!</label>
        <button v-if="jsonended == true" @click="changestruct()"> Change structure</button>
      </div>

      <div class="selector">
        <h2 style="font-size: 40px; margin-top: 26px;" id="Path-text">Expectation Type</h2>
        <button  style="margin-top: -7px;" @click="expectationType = 'value'">Add Value Expectation</button>

        <div v-if="expectationType === 'value'" class="struct">
          <h3>Value Expectation</h3>
          <label for="dataType">Select Type:</label>
          <select style="font-size: 20px;" v-model="selectedDataType" id="dataType">
            <option v-for="option in dataTypes" :key="option.value" :value="option.value">
              {{ option.value }}
            </option>
          </select>
          <div v-if="selectedDataType === 'Timestamp'">
            <button style="margin-top: 10px; margin-bottom: 10px;" @click="selectValue1 = 'seq'">Sequential Expectation</button>
            <button style="margin-bottom: 10px;" @click="selectValue1 = 'range'; selectValue2 = 'choose'">
              Time Range Expectation
            </button>

            <div v-if="selectValue1 === 'seq'">
              <button style="margin-top: 10px; margin-bottom: 10px; margin-right: 10px;" @click="savevalueValidation('Ascending')">Ascending</button>
              <button @click="savevalueValidation('Descending')">Descending</button>
            </div>
            <div v-if="selectValue1 === 'range'">
              <select style="font-size: 20px;" v-model="selectValue2" id="rangeType">
                <option v-for="option in rangeOptions" :key="option.value" :value="option.value">
                  {{ option.value }}
                </option>
              </select>
              <input style="height: 30px; font-size: 15px;" v-model="selectValue3" placeholder="Range Value" />
              <button style="margin-top: 10px;" @click="savevalueValidation('Range')">Create Expectation</button>
            </div>
          </div>
          <div v-if="selectedDataType === 'Record_count'">
            <select style="font-size: 20px; margin-top: 10px;" v-model="selectValue2" id="rangeType">
              <option v-for="option in countoptions" :key="option.value" :value="option.value">
                {{ option.value }}
              </option>
            </select>
            <input style="height: 30px; font-size: 15px;" v-model="selectValue3" placeholder="Value" />
            <input v-if="selectValue2 === 'Min_Max'" style="margin-left: 110px; height: 30px; font-size: 15px;"
              v-model="selectValue4" placeholder="Value2" />
            <button style="margin-top: 10px;" @click="savevalueValidation('Record_Count')">Create Expectation</button>
          </div>
          <div v-if="selectedDataType === 'String_value'">
            <label v-if="!selectedPath.length">No Path selected!</label>
            <label v-if="selectedPath.length">Path:</label>
            <p>{{ selectedPath.join('.') }}</p>
            <input v-if="selectedPath.length" style="height: 30px; font-size: 15px;" v-model="selectValue3"
              placeholder="Value1" />
            <button style="margin-top: 10px;" @click="savevalueValidation('String_value')">Create Expectation</button>

          </div>
          <div v-if="selectedDataType === 'Integer_value'">
            <label v-if="!selectedPath.length">No Path selected!</label>
            <label v-if="selectedPath.length">Path:</label>
            <p>{{ selectedPath.join('.') }}</p>
            <input v-if="selectedPath.length" style="height: 30px; font-size: 15px;" v-model="selectValue3"
              placeholder="Value1" />
            <button style="margin-top: 10px;" @click="savevalueValidation('Integer_value')">Create Expectation</button>

          </div>
          <div v-if="end">
            <p>Expectation Created: {{ expectationMessage }}</p>
          </div>
        </div>
      </div>
      <div class="struct saved">
        <h3 style="font-size: 40px; margin-top: 26px;">Newest VLA:</h3>
        <label v-if="!proValidation.length"> No validations yet!</label>
        <div v-if="proValidation.length">
          <ul>
            <li style="margin-bottom: 20px;" v-for="(validation, index) in proValidation" :key="index">
              {{ validation.type }} - {{ validation.path.join('.') }} -
              <span v-if="Array.isArray(validation.value)">
                {{ validation.value.join(', ') }}
              </span>
              <span v-else>
                {{ validation.value }}
              </span>
              <button style="margin-left: 25px;" v-if="validation.type !== 'Syntax'" @click="removeval(index)">Remove</button>
            </li>
          </ul>
          <button @click="clearValidation">Delete VLA</button>
          <button style="margin-left: 20px;" v-if="!this.getprosign()" @click="sign">Sign</button>
          <button style="margin-left: 20px;" v-if="this.getprosign()" @click="sendVlaToConsumer">Send VLA to Consumer</button>
        </div>
        <label v-if="this.getprosign()">Signed!</label>
      </div>
    </div>
  </div>
  <div class="bottom">
    <div class="bottom-item start" v-if="!moreExpectations && jsonloaded">
      <label>VLA from Consumer:</label>
      <div v-if="proValidation.length">
        <ul>
          <li style="margin-bottom: 15px;" v-for="(validation, index) in proValidation" :key="index">
            {{ validation.type }} - {{ validation.path.join('.') }} -
            <span v-if="Array.isArray(validation.value)">
              {{ validation.value.join(', ') }}
            </span>
            <span v-else>
              {{ validation.value }}
            </span>
            <div v-if="!validation.status">
              <button style="margin-top: 20px; margin-bottom: 10px; margin-right: 25px;" @click="acceptValidation(index)">Accepted</button>
              <button @click="removeValidation(index)">Removed</button>
            </div>
            <label>
              {{ validation.status }}
            </label>
          </li>
        </ul>
      </div>
      <div class="bottom-item center" v-if="conf.length">
        <label>Possible Conflicting Changes:</label>
        <li v-for="(validation, index) in grouped" :key="index">
          {{ validation.path[0].join('.') }} - {{ validation.type[0] }} - <span v-if="Array.isArray(validation.value)">
            {{ validation.value[0].join(', ') }}
          </span>
          <span v-else>
            {{ validation.value[0] }}
          </span> : {{ validation.path[1].join('.') }} - {{
            validation.type[1] }} - <span v-if="Array.isArray(validation.value)">
            {{ validation.value[1].join(', ') }}
          </span>
          <span v-else>
            {{ validation.value[0] }}
          </span>
          <div v-if="!validation.status">
            <button @click="conflictchoose(index, 0)">Choose First Expectation</button>
            <button @click="conflictchoose(index, 1)">Choose Second Expectation</button>
          </div>
          <label>
            {{ validation.status }}
          </label>
        </li>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import JsonTree from '../JsonTree.vue';
import { sharedState } from '../sharedState.js';
export default {
  name: 'ProducerView',
  components: { JsonTree },
  data() {
    return {
      proValidation: [],
      jsonStructure: null,
      showJson: false,
      selectedPath: [],
      selectedType: '',
      selectedDataType: 'String',
      vlacreate: false,
      moreExpectations: false,
      buttonsClicked: 0,
      signed: false,
      conf: [],
      grouped: [],
      valueadd: false,
      valuechoose: 0,
      expectationType: null,
      selectValue1: null,
      selectValue2: "",
      selectValue3: "",
      selectValue4: "",
      end: false,
      expectationMessage: "",
      jsonended: sharedState.jsonend,
      types: {},
      dataTypessyntax: [
        { value: "String" },
        { value: "Integer" },
        { value: "Boolean" },
        { value: "Required" },
      ],
      dataTypes: [
        { value: "Timestamp" },
        { value: "Record_count" },
        { value: "String_value" },
      ],
      rangeOptions: [
        { value: "Day" },
        { value: "Week" },
        { value: "Month" },
      ],
      countoptions: [
        { value: "Min" },
        { value: "Max" },
        { value: "Min_Max" },
        { value: "Exact" },
      ],
    };
  },
  created() {
    if(sharedState.new == true){
      this.createnew();
    }
    if (sharedState.consign == true && sharedState.prosign == true) {
      this.getConflicts();
      this.createExpectation();
    }
    if (sharedState.jsonfile) {
      this.jsonStructure = sharedState.jsonfile;
      this.jsonloaded = true;
    }
    this.getjsontypes();
    this.getConflicts();
  },

  methods: {
    async getjsontypes() {
      this.jsonStructure = this.buildJsonFromTypes(sharedState.types);
    },
    buildJsonFromTypes(types) {
      const result = {};
      let urlbuffer = [];
      let isurl = false;
      types.forEach(({ path, type }) => {
        const keys = path.split('.');
        let current = result;
        keys.forEach((key, index) => {
          if(key.includes("http") || isurl){
            urlbuffer.push(key);
            isurl = true;
            if (index === keys.length - 1) {
            key = urlbuffer.join('.');
            urlbuffer = [];
            isurl = false;
        }
          }
          if(!isurl){
          if (!current[key]) {
            current[key] = index === keys.length - 1 ? this.mapTypeToValue(type) : {};
          }
          current = current[key];
        }
        });
      });
      return result;
    },
    mapTypeToValue(type) {
      switch (type) {
        case 'string':
          return "";
        case 'integer':
          return 0;
        case 'boolean':
          return false;
        case 'array':
          return { };
        default:
          return null;
      }
    },
    async loadUploadedJson() {
      if (!this.uploadedFile) {
        alert("Please select a JSON file first.");
        return;
      }

      try {
        const fileContent = await this.uploadedFile.text();

        sharedState.jsonfile = JSON.parse(fileContent);
        this.jsonloaded = true;
        this.$router.push('/consumer');

      } catch (error) {
        console.error("Error reading or parsing the JSON file:", error);
        alert("Failed to load the JSON file. Please make sure it is a valid JSON.");
      }
    },
    savestruct() {
      this.savesyntaxValidation();
      this.jsonended = true;
      sharedState.jsonend = true;
      this.$refs.jsonTree.saveJsonStructure();
    },
    changestruct() {
      this.proValidation.forEach((value, index) => {
        if (value.type == "Syntax") {
          this.proValidation.splice(index, 1);
        }
      });
      this.jsonended = false;
      sharedState.jsonend = false;
      this.showJson = true;
      this.designcon();
    },
    handleFileUpload(event) {
      this.uploadedFile = event.target.files[0];
      this.loadUploadedJson();
    },
    async getConflicts() {
      const rawData = sharedState.savedValidations.map(validation => {
        return {
          path: validation.path.map(p => p),
          type: validation.type,
          value: Array.isArray(validation.value) ? validation.value : [validation.value],
        };
      });

      try {
        const response = await axios.post('http://localhost:8080/api/get-conflict', rawData);
        const conflicts = response.data;
        conflicts.conflict.forEach(validation => {
          this.conf.push({
            path: validation.path,
            type: validation.type[0],
            value: Array.isArray(validation.value) ? validation.value : [validation.value],
          });
        });
        conflicts.nonConflicts.forEach(validation => {
          this.proValidation.push({
            path: validation.path,
            type: validation.type[0],
            value: Array.isArray(validation.value) ? validation.value : [validation.value],
          });
        });
      } catch (error) {
        console.error("Error loading conflicts:", error);
      }
      this.groupedValidations();
    },
    groupedValidations() {
      let grouped = [];
      let currentGroup = { path: [], type: [], value: [] };

      this.conf.forEach((validation, index) => {
        if (index % 2 === 0 && index > 0) {
          grouped.push(currentGroup);
          currentGroup = { path: [], type: [], value: [] };
        }

        currentGroup.path.push(validation.path);
        currentGroup.type.push(validation.type);
        currentGroup.value.push(validation.value);
      });

      if (currentGroup.path.length > 0) {
        grouped.push(currentGroup);
      }
      this.grouped = grouped;

    },
    conflictchoose(index, i) {
      this.designcon();
      this.proValidation.push({
        path: this.grouped[index].path[i],
        type: this.grouped[index].type[i],
        value: Array.isArray(this.grouped[index].value[i]) ? this.grouped[index].value[i] : [this.grouped[index].type[i]],
        status: ': Accepted',
      });
      this.grouped.splice(index, "1");
      this.buttonsClicked = this.buttonsClicked + 1;
      this.checkAllButtonsClicked();
    },
    acceptValidation(index) {
      const validation = this.proValidation[index];
      validation.status = ': Accepted';
      this.buttonsClicked = this.buttonsClicked + 1;
      this.checkAllButtonsClicked();
    },
    removeValidation(index) {
      this.designcon();
      this.proValidation.splice(index, 1);
      this.checkAllButtonsClicked();
    },
    checkAllButtonsClicked() {
      if (this.proValidation.length + this.grouped.length == this.buttonsClicked) {
        this.moreExpectations = true;
      }
    },
    async loadJsonStructure() {
      try {
        this.jsonStructure = sharedState.jsonfile;
      } catch (error) {
        console.error("Error loading JSON structure:", error);
      }
    },
    removeval(index) {
      this.designcon();
      this.proValidation.splice(index, 1)
    },
    toggleJsonStructure() {
      this.showJson = !this.showJson;
    },
    handleKeySelection({ path, type }) {
      this.selectedPath = path;
      this.selectedType = type;
    },
    savesyntaxValidation() {
      this.buildval("Syntax", ["saved"], "Change under Json Structure")
    },
savevalueValidation(exp) {
      switch (exp) {
        case "Ascending":
          this.buildval("Sequential", ["Sequential_Timestamp"], exp);
          break;
        case "Descending":
          this.buildval("Sequential", ["Sequential_Timestamp"], exp);
          break;
        case "Range":{
          const parsed = parseInt(this.selectValue3, 10);
          if (!isNaN(parsed) && Number.isInteger(parsed) && String(parsed) === this.selectValue3.trim()){
            this.buildval("Timestamp_Within_Range", [this.selectValue2], this.selectValue3);
          }
          else{
            alert("Timestamp can't be String!");
          }
          this.selectValue3 = "";
          break;
        }
        case "Record_Count":
          if (this.selectValue2 == "Min_Max") {
            if (Number(this.selectValue3) >= Number(this.selectValue4)) {
              alert("Minimum can't be greater or equal than Maximum!");
              this.selectValue3 = "";
              this.selectValue4 = "";
            }
            else {
              this.buildval("Record_Count", [this.selectValue2], [this.selectValue3, this.selectValue4]);
            }
          }
          else {
            this.buildval("Record_Count", [this.selectValue2], this.selectValue3);
          }
          break;
        case "String_value":
          if(this.selectValue3 != ""){
            this.buildval("String_value", this.selectedPath, this.selectValue3);
          }
          else{
            alert("String_value can't be null!");
            this.selectValue3 = "";
          }
          break;
        case "Integer_value":
          this.buildval("Integer_value", this.selectedPath, this.selectValue3);
          break;
      }
    },
    buildval(valtype, valpath, valvalue) {
      this.designcon();
      this.proValidation.push({
        path: valpath,
        type: valtype,
        value: Array.isArray(valvalue) ? valvalue : [valvalue],
      });
      this.selectedPath = [];
      this.selectedType = '';
      this.selectedDataType = 'choose';
      this.syntax = false;
      this.value = false;
      this.selectValue1 = "";
      this.selectValue2 = "";
      this.selectValue3 = "";
      this.selectValue4 = "";
    },
    clearValidation() {
      this.designcon();
      this.proValidation = []
      this.selectedPath = [];
      this.selectedType = '';
      this.selectedDataType = 'String';
      this.vlacreate = false;
    },
    createVLA() {
      this.vlacreate = true;
    },

    async createExpectation() {

      let paths = sharedState.savedValidations
        .filter(validation => !this.arraysAreEqual(validation.path, ["saved"]))
        .map(validation => validation.path);

      let expectations = sharedState.savedValidations
        .filter(validation => validation.type !== "Syntax")
        .map(validation => [validation.type]);

      let values = sharedState.savedValidations
        .filter(validation => !this.arraysAreEqual(validation.value, ["Change under Json Structure"]))
        .map(validation => validation.value);
      let typepath = sharedState.types.map(validation => [validation.path]);
      let typevalue = sharedState.types.map(validation => [validation.type]);
      try {
        await axios.post('http://localhost:8080/api/create-expectation', { paths, expectations, values, typepath, typevalue });
        if (window.confirm("Expectation created successfully!\n\nGo to the API JSON?")) {
        window.open("http://localhost:8080/api/json", "_blank");
      }
        this.proValidation = [];
        this.vlacreate = false;
      } catch (error) {
        console.error("Error creating expectation:", error);
      }
      this.createnew();  
    },
    createnew(){
      this.jsonloaded = false;
      sharedState.consign = false;
      sharedState.prosign = false;
      sharedState.types = [];
      sharedState.first = false;
      sharedState.jsonfile = null;
      sharedState.savedValidations = [];
      sharedState.jsonend = false;
      sharedState.fullpaths = [];
      sharedState.new = false;
    },
    arraysAreEqual(arr1, arr2) {
      if (arr1.length !== arr2.length) return false;
      return arr1.every((item, index) => item === arr2[index]);
    },
    async sendVlaToConsumer() {
      sharedState.savedValidations.splice(0);
      this.proValidation.forEach(validation => {
        sharedState.savedValidations.push({
          path: validation.path,
          type: validation.type,
          value: validation.value,
        });
      });
      this.$router.push('/consumer');
    },
    sign() {
      sharedState.prosign = true;
    },
    designcon() {
      sharedState.consign = false;
    },
    getprosign() {
      return sharedState.prosign;
    }
  }
}; 
</script>
<style scoped>
h1{
  font-weight: 400;
}
h2{
  font-weight: 400;
}
h3{
  font-weight: 400;
}
button {
    font-size: 22px;
    background-color: #17243F;
    border-radius: 5px;
    cursor: pointer;
    color: white;
    padding: 8px 8px 8px 8px;
}

.file-input {
  display: none;
}

.custom-file-label {
  font-size: 25px;
  font-family: 'Arial', sans-serif;
  background-color: #17243F;
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}


.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
}

.header-label {
  height: 30px;
  padding: 65px;
  flex-grow: 1.3;
  text-align: center;
  font-size: 30px;
  color: white;
  background-color: #17243F;
  padding-left: 10px;
}

.header_role {
  height: 30px;
  padding: 65px;
  flex-grow: 0.1;
  font-size: 30px;
  color: white;
  background-color: #17243F;
}

.temp {
  display: flex;
  flex-direction: row;
  gap: 400px;
}

.struct {
  font-size: 25px;
  flex-flow: column;
}

.json {
  width: 100%;
}

.selector {
  width: 15%;
  font-size: 25px;
}

.saved {
  font-size: 25px;

}

#Path-text {
  margin-right: 12%;
}

.bottom {
  display: flex;
  align-items: flex-start;
  margin-top: 50px;
  background-color: #f8f8f8;
}

.bottom-item {
  width: 50%;
}

.bottom-item.start {
  font-size: 25px;

}

.bottom-item.center {
  text-align: left;
  font-size: 25px;

}
</style>