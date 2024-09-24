import axios from 'axios';
const axiosInstance = axios.create();

axiosInstance.interceptors.request.use(config => {
    if (localStorage.getItem('userToken')) {
        config.headers.Authorization = localStorage.getItem('userToken');
    } else if (localStorage.getItem('supervisorToken')) {
        config.headers.Authorization = localStorage.getItem('supervisorToken');
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default axiosInstance;