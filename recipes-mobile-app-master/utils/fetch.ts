import axios from 'axios';

const sendRequest = axios.create({
  baseURL: 'http://192.168.1.220:8881/api',
  validateStatus: () => true,
  headers: {
    'Content-Type': 'application/json',
  },
});

export { sendRequest };
