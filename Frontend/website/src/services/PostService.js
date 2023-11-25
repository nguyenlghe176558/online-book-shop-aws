import axios from "axios";

const API_URL = `${process.env.BACKEND_API_URL}/post`;

const getAllPostCategories = () => {
    return axios.get(process.env.BACKEND_API_URL + "/post-category");
}

const getPostByCategory = (categoryId, page) => {
    return axios.get(API_URL + `?category=${categoryId}&page=${page-1}&size=10`);
}

const getPostById = (postId) => {
    return axios.get(API_URL + `/${postId}`);
}

export {getAllPostCategories, getPostByCategory, getPostById}