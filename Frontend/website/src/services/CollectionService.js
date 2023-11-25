import axios from "axios";

const COLLECTION_URL_API = `${process.env.BACKEND_API_URL}/book-collection`;

const getCollections = () => {
    return axios.get(COLLECTION_URL_API)
}

const getCollectionById = (id) => { 
    return axios.get(COLLECTION_URL_API + '/' + id)
}

export {getCollections, getCollectionById}