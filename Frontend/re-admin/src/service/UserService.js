import axios from "axios";

const ACCOUNT_BASE_URL = "https://backend.sachtructuyen.shop/api/v1/auth/";

const login = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'authenticate', account);
}

const getUserInfoByEmail = (email) => {
    return axios.get(`https://backend.sachtructuyen.shop/api/v1/user/by-email/${email}`);
}

const getAllUser = async () => {
    return axios.get(`https://backend.sachtructuyen.shop/api/v1/user/customer`);
}

const changePassword = (data) => {
    return axios.post("https://backend.sachtructuyen.shop/api/v1/auth/change-password", data)

}


export { login, getUserInfoByEmail, getAllUser, changePassword }