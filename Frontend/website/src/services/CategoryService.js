import axios from "axios";

const CATEGORIES_API_URL = `${process.env.BACKEND_API_URL}/book-category`;

const getCategories = () => {
    return axios.get(CATEGORIES_API_URL)
}

export {getCategories}