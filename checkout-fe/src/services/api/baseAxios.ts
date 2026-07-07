import axios from 'axios';

// Tenant context is derived server-side from the JWT via the gateway
// (X-Auth-Tenant, RFC-03); the frontend no longer sends a tenant header.
const baseAxios = axios.create({
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

export default baseAxios;
