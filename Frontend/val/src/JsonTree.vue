<template>
  <ul v-if="jsonended == false">
    <li v-for="(value, key) in node" :key="key">
      <div>
        <div @click="handleNodeClick(key, value)">
          <span>{{ key }}</span>
          <span v-if="isExpandable(value)"> &#9654; </span>
        </div>
        <div style="height: 25px; margin-left: 20px">
          <select style="font-size: 20px;" v-if="typeMap[pathKey([...path, key])] !== undefined "
            v-model="typeMap[pathKey([...path, key])]" @change="updateType([...path, key])">
            <option v-for="option in types" :key="option" :value="option">
              {{ option }}
            </option>
          </select>
        </div>

        <JsonTree v-if="isExpandable(value) && expandedKeys.includes(key)" :node="value" :path="[...path, key]"
          @keySelected="$emit('keySelected', $event)" />
      </div>
    </li>
  </ul>


</template>

<script>
import { sharedState } from './sharedState';
export default {
  name: 'JsonTree',
  props: {
    node: { type: Object, required: true },
    path: { type: Array, default: () => [] },
    type: { type: Object, required: true },
  },

  data() {
    return {
      jsonstructure: [],
      expandedKeys: [],
      valuetypes: [],
      types: ["String", "Integer", "Boolean", "Array", "Object"],
      typeMap: [],
      jsonended: sharedState.jsonend,
    };
  },

  created() {
    this.valuetypes = sharedState.types;
    this.initializeTypeMap();
  },

  methods: {
    handleSaveStruct() {
      this.$refs.jsonTree.someMethod();
    },

    saveJsonStructure() {
      sharedState.types = this.valuetypes;
    },

    initializeTypeMap() {
      this.valuetypes.forEach(entry => {
        var replacedpath = entry.path
        this.typeMap[replacedpath] = entry.type
      });
    },

    pathKey(path) {
      return path.join('.');
    },

    updateType(path) {
  const pathString = path.join('.');
  const newType = this.typeMap[pathString];

  const existingIndex = this.valuetypes.findIndex(entry => entry.path === pathString);
  if (existingIndex !== -1) {
    this.valuetypes[existingIndex].type = newType;
  } else {
    this.valuetypes.push({ path: pathString, type: newType});
  }
  sharedState.types = this.valuetypes;

}
,

    isExpandable(value) {
      return typeof value === 'object' && value !== null && !Array.isArray(value);
    },

    handleNodeClick(key, value) {
      if (!this.isExpandable(value)) {
        this.selectKey(key, value);
      } else {
        this.toggleExpand(key);
      }
    },

    selectKey(key, value) {
      this.$emit('keySelected', {
        path: [...this.path, key],
        type: Array.isArray(value) ? 'Array' : typeof value,
      });
    },

    toggleExpand(key) {
      if (this.expandedKeys.includes(key)) {
        this.expandedKeys = this.expandedKeys.filter((k) => k !== key);
      } else {
        this.expandedKeys.push(key);
      }
    },
  },
};
</script>

<style scoped>
div {
  display: flex;
}

li {
  cursor: pointer;
}

button {
  margin: 5px;
  height: 30px;
  font-size: 15px;
}

.jsonend {
  display: flex;
  flex-direction: column;
}
</style>
