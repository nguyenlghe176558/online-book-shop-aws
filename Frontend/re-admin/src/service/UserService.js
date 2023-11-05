import axios from "axios";

const ACCOUNT_BASE_URL = "https://backend.sachtructuyen.shop/api/v1/auth/";

const login = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'authenticate', account);
}

const getUserInfoByEmail = (email) => {
    return axios.get(`http://localhost:8081/api/v1/user/by-email/${email}`);
}


export {login, getUserInfoByEmail}