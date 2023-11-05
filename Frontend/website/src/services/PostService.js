import axios from "axios";

const API_URL = "https://backend.sachtructuyen.shop/api/v1/post";

const getAllPostCategories = () => {
    return axios.get("https://backend.sachtructuyen.shop/api/v1/post-category");
}

const getPostByCategory = (categoryId, page) => {
    return axios.get(API_URL + `?category=${categoryId}&page=${page-1}&size=10`);
}

const getPostById = (postId) => {
    return axios.get(API_URL + `/${postId}`);
}

export {getAllPostCategories, getPostByCategory, getPostById}