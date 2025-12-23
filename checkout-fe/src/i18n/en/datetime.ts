import type { IntlDateTimeFormat } from 'vue-i18n';

const datetimeFormats: IntlDateTimeFormat = {
  short: {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  },
  long: {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  },
  iso: {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  },
};

export default datetimeFormats;
