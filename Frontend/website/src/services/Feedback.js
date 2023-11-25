import axios from "axios";

const API_URL = `${process.env.BACKEND_API_URL}/feedback`;

const getAllFeedback = () => {
    return axios.get(API_URL);
}

const getFeedbackById = (feedbackId) => {
    return axios.get(API_URL + `/${feedbackId}`);
}

const answerFeedback = (feedback) => {
    return axios.post(API_URL, feedback);
}

const getFeedbackByBookId = (bookId) => {
    return axios.get(`${process.env.BACKEND_API_URL}/feedback/by-book?book=${bookId}&sortBy=id&page=0&size=10&sortOrder=asc`);
}

export {getAllFeedback, getFeedbackById, answerFeedback, getFeedbackByBookId}