<template>
  <div class="header">
    <img src="../assets/images.png" alt="Header Image" />
    <label class="header-label">Veracity Level Agreement Creating Site</label>
    <label class="header_role">Role: Consumer</label>
  </div>
  <hr />
  <div v-if="moreExpectations">
    <div class="temp">
      <div class="Json">
        <h1>JSON Structure</h1>
        <button v-if="!jsonended" @click="toggleJsonStructure">
          {{ showJson ? 'Hide JSON Structure' : 'Load JSON Structure' }}
        </button>
        <div class="struct" v-if="showJson && !jsonended && jsonStructure && Object.keys(jsonStructure).length">
          <JsonTree ref="jsonTree" :node="jsonStructure" :path="[]" @keySelected="handleKeySelection"
            @updateJson="updateJsonStructure" />
        </div>
        <button v-if="showJson && !jsonended" @click="savestruct">Save Struct</button>
        <label style="font-size: 25px;" v-if="jsonended == true"> Json structure saved!</label>
        <button v-if="jsonended == true" @click="changestruct()"> Change structure</button>
      </div>

      <div class="selector">
        <h2 id="Path-text">Expectation Type</h2>
        <button @click="expectationType = 'value'">Add Value Expectation</button>



        <div v-if="expectationType === 'value'" class="struct">
          <h3>Value Expectation</h3>
          <label for="dataType">Select Type:</label>
          <select style="font-size: 20px;" v-model="selectedDataType" id="dataType">
            <option v-for="option in dataTypes" :key="option.value" :value="option.value">
              {{ option.value }}
            </option>
          </select>
          <div v-if="selectedDataType === 'Timestamp'">
            <button @click="selectValue1 = 'seq'">Sequential Expectation</button>
            <button @click="selectValue1 = 'range'; selectValue2 = 'choose'">
              Time Range Expectation
            </button>

            <div v-if="selectValue1 === 'seq'">
              <button @click="savevalueValidation('Ascending')">Ascending</button>
              <button @click="savevalueValidation('Descending')">Descending</button>
            </div>
            <div v-if="selectValue1 === 'range'">
              <select style="font-size: 20px;" v-model="selectValue2" id="rangeType">
                <option v-for="option in rangeOptions" :key="option.value" :value="option.value">
                  {{ option.value }}
                </option>
              </select>
              <input style="height: 30px; font-size: 15px;" v-model="selectValue3" placeholder="Range Value" />
              <button @click="savevalueValidation('Range')">Create Expectation</button>
            </div>
          </div>
          <div v-if="selectedDataType === 'Record_count'">
            <select style="font-size: 20px;" v-model="selectValue2" id="rangeType">
              <option v-for="option in countoptions" :key="option.value" :value="option.value">
                {{ option.value }}
              </option>
            </select>
            <input style="height: 30px; font-size: 15px;" v-model="selectValue3" placeholder="Value" />
            <input v-if="selectValue2 === 'Min_Max'" style="margin-left: 110px; height: 30px; font-size: 15px;"
              v-model="selectValue4" placeholder="Value2" />
            <button @click="savevalueValidation('Record_Count')">Create Expectation</button>
          </div>
          <div v-if="selectedDataType === 'String_value'">
            <label v-if="!selectedPath.length">No Path selected!</label>
            <label v-if="selectedPath.length">Path:</label>
            <p>{{ selectedPath.join('.') }}</p>
            <input v-if="selectedPath.length" style="height: 30px; font-size: 15px;" v-model="selectValue3"
              placeholder="Value1" />
            <button @click="savevalueValidation('String_value')">Create Expectation</button>

          </div>
          <div v-if="selectedDataType === 'Integer_value'">
            <label v-if="!selectedPath.length">No Path selected!</label>
            <label v-if="selectedPath.length">Path:</label>
            <p>{{ selectedPath.join('.') }}</p>
            <input v-if="selectedPath.length" style="height: 30px; font-size: 15px;" v-model="selectValue3"
              placeholder="Value1" />
            <button @click="savevalueValidation('Integer_value')">Create Expectation</button>

          </div>
          <div v-if="end">
            <p>Expectation Created: {{ expectationMessage }}</p>
          </div>
        </div>
      </div>

      <div class="struct saved">
        <h3>Newest VLA:</h3>
        <label v-if="!conValidation.length"> No validations yet!</label>
        <div v-if="conValidation.length">
          <ul>
            <li v-for="(validation, index) in conValidation" :key="index">
              {{ validation.type }} - {{ validation.path.join('.') }} -
              <span v-if="Array.isArray(validation.value)">
                {{ validation.value.join(', ') }}
              </span>
              <span v-else>
                {{ validation.value }}
              </span>
              <button v-if="validation.type !== 'Syntax'" @click="removeval(index)">Remove</button>
            </li>
          </ul>
          <button @click="clearValidation">Delete VLA</button>
          <button v-if="!this.getconsign()" @click="sign">Sign</button>
          <button v-if="this.getconsign()" @click="sendVlaToProducer">Send VLA to Producer</button>

        </div>
        <label v-if="this.getconsign()">Signed!</label>
      </div>
    </div>
  </div>


  <div class="bottom">
    <div class="bottom-item start" v-if="!moreExpectations">
      <label>VLA from Producer:</label>
      <div v-if="conValidation.length">
        <ul>
          <li v-for="(validation, index) in conValidation" :key="index">
            {{ validation.type }} - {{ validation.path.join('.') }} -
            <span v-if="Array.isArray(validation.value)">
              {{ validation.value.join(', ') }}
            </span>
            <span v-else>
              {{ validation.value }}
            </span>
            <div v-if="!validation.status">
              <button v-if="validation.type !== 'Syntax'" @click="acceptValidation(index)">Accepted</button>
              <button v-if="validation.type !== 'Syntax'" @click="removeValidation(index)">Removed</button>
            </div>
            <label>
              {{ validation.status }}
            </label>
          </li>
        </ul>
      </div>
      <div class="bottom-item center" v-if="conflict.length">
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
  name: 'ConsumerView',
  components: { JsonTree },
  data() {
    return {
      jsonStructure: null,
      showJson: false,
      selectedPath: [],
      selectedType: '',
      selectedDataType: '',
      vlacreate: false,
      moreExpectations: true,
      buttonsClicked: 0,
      signed: false,
      conValidation: [],
      conflict: [],
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

    if (sharedState.consign == true && sharedState.prosign == true) {
      this.getConflicts();
      this.createExpectation();
    }
    if (sharedState.first == true) {
      this.getConflicts();
      this.moreExpectations = false;
    }
    if(sharedState.first == false){
      this.getjsontypes();
    }
    this.jsonStructure = sharedState.jsonfile;
  },
  methods: {
    async getjsontypes() {
      const response = await axios.post('http://localhost:8080/api/json_types', sharedState.jsonfile);
      const json = response.data;
      sharedState.types = json;
    },
    async getConflicts() {
      const rawData = sharedState.savedValidations.map(validation => {
        return {
          path: validation.path.map(p => p),
          type: validation.type,
          value: [...validation.value],
        };
      });
      try {
        const response = await axios.post('http://localhost:8080/api/get-conflict', rawData);
        const conflicts = response.data;
        conflicts.conflict.forEach(validation => {
          this.conflict.push({
            path: validation.path,
            type: validation.type[0],
            value: Array.isArray(validation.value) ? validation.value : [validation.value],
          });
        });
        conflicts.nonConflicts.forEach(validation => {
          this.conValidation.push({
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

      this.conflict.forEach((validation, index) => {
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
      this.designpro();
      this.conValidation.push({
        path: this.grouped[index].path[i],
        type: this.grouped[index].type[i],
        value: Array.isArray(this.grouped[index].value[i]) ? this.grouped[index].value[i] : [this.grouped[index].type[i]],
        status: ': Accepted',
      });
      this.grouped.splice(index, "1");
      this.buttonsClicked = this.buttonsClicked + 1;
      this.checkAllButtonsClicked();
    },
    savesyntaxValidation() {
      this.buildval("Syntax", ["saved"], "Change under Json Structure")
    },
    savevalueValidation(exp) {
      switch (exp) {
        case "Ascending":
          this.buildval("Sequiental", ["Sequential_Timestamp"], exp);
          break;
        case "Descending":
          this.buildval("Sequiental", ["Sequential_Timestamp"], exp);
          break;
        case "Range":
          this.buildval("Timestamp_Within_Range", [this.selectValue2], this.selectValue3);
          break;
        case "Record_Count":
          if (this.selectValue2 == "Min_Max") {
            this.buildval("Record_Count", [this.selectValue2], [this.selectValue3, this.selectValue4]);
          }
          else {
            this.buildval("Record_Count", [this.selectValue2], this.selectValue3);
          }
          break;
        case "String_value":
          this.buildval("String_value", this.selectedPath, this.selectValue3);
          break;
        case "Integer_value":
          this.buildval("Integer_value", this.selectedPath, this.selectValue3);
          break;
      }
    },
    buildval(valtype, valpath, valvalue) {
      this.designpro();
      this.conValidation.push({
        path: valpath,
        type: valtype,
        value: Array.isArray(valvalue) ? valvalue : [valvalue],
      });
      this.selectedPath = [];
      this.selectedType = '';
      this.selectedDataType = 'choose';
      this.syntax = false;
      this.value = false;
      this.selectValue1 = null;
      this.selectValue2 = "";
      this.selectValue3 = "";
      this.selectValue4 = "";
    },
    acceptValidation(index) {
      const validation = this.conValidation[index];
      validation.status = ': Accepted';
      this.buttonsClicked = this.buttonsClicked + 1;
      this.checkAllButtonsClicked();
    },
    removeValidation(index) {
      this.designpro();
      this.conValidation.splice(index, 1);
      this.checkAllButtonsClicked();
    },
    checkAllButtonsClicked() {
      if (this.conValidation.length + this.grouped.length == this.buttonsClicked) {
        this.moreExpectations = true;
      }
    },
    savestruct() {
      this.savesyntaxValidation();
      this.jsonended = true;
      sharedState.jsonend = true;
      this.$refs.jsonTree.saveJsonStructure();
    },
    removeval(index) {
      this.designpro();
      this.conValidation.splice(index, 1)
    },
    toggleJsonStructure() {
      this.showJson = !this.showJson;
    },
    handleKeySelection({ path, type }) {
      this.selectedPath = path;
      this.selectedType = type;
    },

    clearValidation() {
      this.designpro();
      this.conValidation = []
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
        .filter(validation =>!this.arraysAreEqual(validation.path, ["saved"]))
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
        await axios.post('http://localhost:8080/api/create-expectation', { paths, expectations, values, typepath,typevalue });
        alert("Expectation created successfully!");
        this.conValidation = [];
        this.vlacreate = false;
      } catch (error) {
        console.error("Error creating expectation:", error);
      }
      this.$router.push('/validate');
    },
    arraysAreEqual(arr1, arr2) {
      if (arr1.length !== arr2.length) return false;
      return arr1.every((item, index) => item === arr2[index]);
    },
    sendVlaToProducer() {
      sharedState.first = true;
      sharedState.savedValidations.splice(0);
      this.conValidation.forEach(validation => {
        sharedState.savedValidations.push({
          path: validation.path,
          type: validation.type,
          value: Array.isArray(validation.value) ? validation.value : [validation.value],
        });
      });
      this.$router.push('/producer');
    },
    sign() {
      sharedState.consign = true;
    },
    designpro() {
      sharedState.prosign = false;
    },
    getconsign() {
      return sharedState.consign;
    },
    changestruct() {
      this.conValidation.forEach((value, index) => {
        if (value.type == "Syntax") {
          this.conValidation.splice(index, 1);
        }
      });
      this.jsonended = false;
      sharedState.jsonend = false;
      this.showJson = true;
      this.designpro();
    },
  },
};
</script>

<style scoped>
button {
  margin: 5px;
  height: 30px;
  font-size: 15px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: darkgray;
}

.header-label {
  flex-grow: 1.3;
  text-align: center;
  font-size: 30px;
  font-weight: bold;
}

.header_role {
  flex-grow: 0.1;
  font-size: 30px;
  font-weight: bold;
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

.value {
  margin-top: 250px;
}
</style>