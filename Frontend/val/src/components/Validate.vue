<template>
    <div class="header">
        <img src="../assets/images.png" alt="Header Image" />
        <label class="header-label">Veracity Level Agreement Validation Site</label>
        <label class="header_role">Role: Consumer</label>
    </div>
    <hr />
    <div class="temp">
        <div class="structval">
            <h3>Final VLA:</h3>
            <div>
                <ul>
                    <li v-for="(validation, index) in finalVal" :key="index">
                        <span v-if="validation.path == 'saved'">
                            {{ validation.path.join('.') }} - {{ validation.type }}
                        </span>
                        <span v-else>
                        {{ validation.path.join('.') }} - {{ validation.type }} - 
                        <span v-if="Array.isArray(validation.value)">
                            {{ validation.value.join(', ') }}
                        </span>
                        <span v-else>
                            {{ validation.value }}
                        </span>
                    </span>
                    </li>
                </ul>
            </div>
            <button class="valbutton" @click="createExpectation" :disabled="loading">
                Validate
            </button>
            <div v-if="loading" class="loading-bar">Validating...</div>
            <div v-if="!loading && validationResults">
                <p>Syntax Validation: 
                    <span :class="{ success: validationResults.syntaxValidation, fail: !validationResults.syntaxValidation }">
                        {{ validationResults.syntaxValidation ? 'Successful' : 'Failed' }}
                    </span>
                </p>
                <p>Value Validation: 
                    <span :class="{ success: validationResults.valueValidation, fail: !validationResults.valueValidation }">
                        {{ validationResults.valueValidation ? 'Successful' : 'Failed' }}
                    </span>
                </p>
                <p style="width: 1000px;">See additional information at: syntax_validation_results.json and value_validation_results.json.
                </p>
            </div>
        </div>
    </div>
</template>


<script>
import axios from 'axios';
import { sharedState } from '../sharedState.js';

export default {
    name: 'ValidateView',
    data() {
        return {
            finalVal: [],
            validationResults: null,
            loading: false,
        };
    },
    created() {
        if (sharedState.savedValidations && sharedState.savedValidations.length > 0) {
            sharedState.savedValidations.forEach(validation => {
                if(validation.path == "saved"){
                    this.finalVal.push({
                    path: validation.path,
                    type: validation.type,
                });
                }
                else{
                    this.finalVal.push({
                    path: validation.path,
                    type: validation.type,
                    value: Array.isArray(validation.value) ? validation.value : [validation.value],
                });
                }
            });
        }
    },
    methods: {
        async createExpectation() {
            this.loading = true;
            this.validationResults = null;

            try {
                const response = await axios.get('http://localhost:8080/api/validate');
                this.validationResults = response.data;
            } catch (error) {
                console.error("Error while validating:", error);
            } finally {
                this.loading = false;
            }
        },
    },
};
</script>

<style>
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

.structval {
    font-size: 25px;
    width: 25%;
    position: relative;
}

.valbutton {
    padding: 10px 20px;
    font-size: 23px;
    background-color: lightblue;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    margin-top: 20px;
}

.valbutton:disabled {
    background-color: grey;
    cursor: not-allowed;
}

.loading-bar {
    margin-top: 20px;
    font-size: 18px;
    color: blue;
}

.success {
    color: green;
    font-weight: bold;
}

.fail {
    color: red;
    font-weight: bold;
}
</style>
