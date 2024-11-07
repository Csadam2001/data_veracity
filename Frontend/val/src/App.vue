<template>
  <div>
    <h1>Database JSON Structure</h1>
    <button @click="loadJsonStructure">Load JSON Structure</button>

    <div v-if="jsonStructure && Object.keys(jsonStructure).length">
      <JsonTree :node="jsonStructure" :path="[]" @keySelected="handleKeySelection" />
    </div>

    <div>
      <h2>Selected Path</h2>
      <p>{{ selectedPath.join('.') }}</p>

      <div>
        <label for="dataType">Select Data Type:</label>
        <select v-model="selectedDataType" id="dataType">
          <option value="String">String</option>
          <option value="Boolean">Boolean</option>
          <option value="Integer">Integer</option>
        </select>
        <button @click="saveValidation">Save Validation</button>
      </div>

      <div v-if="savedValidations.length">
        <h3>Saved Validations</h3>
        <ul>
          <li v-for="(validation, index) in savedValidations" :key="index">
            {{ validation.path.join('.') }} - {{ validation.type }}
          </li>
        </ul>
      </div>
      <button v-if="savedValidations.length" @click="clearValidation">Clear Validations</button>
      <button v-if="savedValidations.length" @click="createExpectation">Create Expectation</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import JsonTree from './JsonTree.vue';

export default {
  components: { JsonTree },
  data() {
    return {
      jsonStructure: null,
      selectedPath: [],
      selectedType: '',
      selectedDataType: 'String', 
      savedValidations: [], 
    };
  },
  methods: {
    async loadJsonStructure() {
      try {
        const response = await axios.get('/api/json-structure');
        this.jsonStructure = response.data;
      } catch (error) {
        console.error("Error loading JSON structure:", error);
      }
    },
    handleKeySelection({ path, type }) {
      this.selectedPath = path;
      this.selectedType = type;
    },
    saveValidation() {
      if (this.selectedPath.length) {
        this.savedValidations.push({
          path: [...this.selectedPath],
          type: this.selectedDataType,
        });
        this.selectedPath = [];
        this.selectedType = '';
        this.selectedDataType = 'String';
      }
    },
    clearValidation(){
      this.savedValidations = []
      this.selectedPath = [];
      this.selectedType = '';
      this.selectedDataType = 'String';
    },
    async createExpectation() {
      const paths = this.savedValidations.map(validation => validation.path);
      const expectations = this.savedValidations.map(validation => [validation.type]);
      console.log(paths);
      console.log(expectations);
      try {
        await axios.post('/api/create-expectation', { paths, expectations });
        alert("Expectation created successfully!");
        this.savedValidations = [];
      } catch (error) {
        console.error("Error creating expectation:", error);
      }
    },
  },
};
</script>

<style scoped>
button {
  margin: 5px;
}
</style>
