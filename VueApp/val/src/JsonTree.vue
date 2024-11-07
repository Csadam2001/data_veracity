<template>
    <ul>
      <li v-for="(value, key) in node" :key="key">
        <div @click="handleNodeClick(key, value)">
          <span>{{ key }}</span>
          <span v-if="isExpandable(value)"> &#9654; </span>
        </div>
  
        <JsonTree
          v-if="isExpandable(value) && expandedKeys.includes(key)"
          :node="value"
          :path="[...path, key]"
          @keySelected="$emit('keySelected', $event)"
        />
      </li>
    </ul>
  </template>
  
  <script>
  export default {
    name: 'JsonTree',
    props: {
      node: { type: Object, required: true },
      path: { type: Array, default: () => [] },
    },
    data() {
      return {
        expandedKeys: [],
      };
    },
    methods: {
      isExpandable(value) {
        return typeof value === 'object' && value !== null && !Array.isArray(value);
      },
      isSelectable(value) {
        return typeof value !== 'object' || (Array.isArray(value));
      },
      handleNodeClick(key, value) {
        if (this.isSelectable(value)) {
          this.selectKey(key, value);
        } else if (this.isExpandable(value)) {
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
          this.expandedKeys = this.expandedKeys.filter(k => k !== key);
        } else {
          this.expandedKeys.push(key);
        }
      },
    },
  };
  </script>
  
  <style scoped>
  li {
    cursor: pointer;
  }
  </style>
  