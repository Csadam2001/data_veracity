import { reactive } from 'vue';

export const sharedState = reactive({
  savedValidations: [],
  first: false,
  consign: false,
  prosign: false,
  jsonfile: null,
  jsonend: false,
  fullpaths: [],
  types: [],
});
