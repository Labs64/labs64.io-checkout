import axios from 'axios';

const tenantId = 'SOME-TENANT';

const baseAxios = axios.create({
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
    'X-Tenant-Id': tenantId,
  },
});

export default baseAxios;
