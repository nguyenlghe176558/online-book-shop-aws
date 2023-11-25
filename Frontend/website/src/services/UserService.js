import axios from "axios";

const ACCOUNT_BASE_URL = `${process.env.BACKEND_API_URL}/auth/`;

const createAccount = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'register', account);
}

const login = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'authenticate', account);
}

const getUserInfoByEmail = (email) => {
    return axios.get(`${process.env.BACKEND_API_URL}/user/by-email/${email}`);
}

const updateUser = (profile) => {
    return axios.put(`${process.env.BACKEND_API_URL}/user`, profile);
}

const forgetPassword = (email) => {
    return axios.post(ACCOUNT_BASE_URL + 'forgot-password', email);
}

const resetPassword = (resetData) => {
    return axios.post(ACCOUNT_BASE_URL + 'reset-password', resetData);
}

const activateAccount = (token) => {
    return axios.post(`${process.env.BACKEND_API_URL}/activation`, token)
}

const changePassword = (data) => {
    return axios.post(`${process.env.BACKEND_API_URL}/change-password`, data)

}

export {createAccount, login, getUserInfoByEmail, updateUser, forgetPassword, resetPassword, activateAccount, changePassword}