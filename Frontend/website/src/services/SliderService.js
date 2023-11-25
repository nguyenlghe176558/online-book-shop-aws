import axios from "axios"

const SLIDER_API_BASE_URL = process.env.BACKEND_API_URL + "/slider"

const getSlider = () => {
    return axios.get(SLIDER_API_BASE_URL)
}

export {getSlider}