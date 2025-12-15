import type { RemoveIndexSignature } from '@intlify/core-base';
import type { Dayjs } from 'dayjs';
import type Swal from 'sweetalert2';
import type { Form, Field, ErrorMessage } from 'vee-validate';
import type { VueGtag } from 'vue-gtag';
import type { ComposerTranslation, DefineLocaleMessage } from 'vue-i18n';
import type { RouteLocationNormalizedLoaded, Router } from 'vue-router';
import type { Messages, Locales } from '@/types/i18n';
import type { ReportedError } from '@/types/plugins/errorReporter';

declare module '@vue/runtime-core' {
  export interface GlobalComponents {
    VForm: typeof Form;
    VField: typeof Field;
    VErrorMessage: typeof ErrorMessage;
  }
}
