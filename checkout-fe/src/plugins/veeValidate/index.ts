/* ============
 * VeeValidate
 * ============
 *
 * VeeValidate is a validation library for Vue.js.
 * It has plenty of validation rules out of the box and support for custom ones as well.
 * It is template based, so it is similar and familiar with the HTML5 validation API.
 *
 * @see https://vee-validate.logaretm.com/v4/
 */

// types
import { localize } from '@vee-validate/i18n';

// rules
import { required, email } from '@vee-validate/rules';

// vee-validate
import { defineRule, configure, Form, Field, ErrorMessage } from 'vee-validate';

// vue
import type { App } from 'vue';

// localization
configure({
  generateMessage: localize('en', {
    messages: {},
  }),
});

// turn on rules
defineRule('required', required);
defineRule('email', email);

export default {
  install(app: App) {
    // define validation components globally
    app.component('VForm', Form);
    app.component('VField', Field);
    app.component('VErrorMessage', ErrorMessage);
  },
};
