import {
    LOGIN_SUCCESS,
    LOGIN_FAIL,
    USER_LOADED_SUCCESS,
    USER_LOADED_FAIL,
    SIGNUP_SUCCESS,
    SIGNUP_FAIL,
    LOGOUT
} from './types';
import axios from 'axios';

// Dispatch store state
export const load_user = () => async dispatch => {
    if (localStorage.getItem('access')) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`,
                'Accept': 'application/json'
            }
        };

        try {
            const res = await axios.get(`${process.env.REACT_APP_BACKEND_API_URL}/api/resource/`, config);
            dispatch({
                type: USER_LOADED_SUCCESS,
                payload: res.data
            });
            return {
                login: true,
                userLoaded: true,
                res
            }
        } catch (err) {
            dispatch({
                type: USER_LOADED_FAIL
            });
            return {
                login: true,
                userLoaded: false,
                err
            }
        }
    } else {
        dispatch({
            type: USER_LOADED_FAIL
        });
        return {
            login: false,
            err: new Error('missing token')
        }
    }
};

export const login = (username, password) => async dispatch => {
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const body = JSON.stringify({ username, password });

    try {
        const res = await axios.post(`${process.env.REACT_APP_BACKEND_API_URL}/api/token/`, body, config);
        dispatch({
            type: LOGIN_SUCCESS,
            payload: res.data
        });

        const loadRes = await dispatch(load_user());
        return loadRes;
    } catch (err) {
        dispatch({
            type: LOGIN_FAIL
        })
        return {
            login: false,
            userLoaded: false,
            err
        }
    }
};

export const logout = () => async dispatch => {
    if (localStorage.getItem('access')) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`,
                'Accept': 'application/json'
            }
        };

        const body = JSON.stringify({ "refresh_token": localStorage.getItem('refresh') });

        try {
            const res = await axios.post(`${process.env.REACT_APP_BACKEND_API_URL}/api/logout/`, body, config);
            dispatch({
                type: LOGOUT,
                payload: res.data
            });

        } catch (err) {
            dispatch({
                type: LOGOUT,
                payload: err
            });
        }
    }
    else {
        dispatch({
            type: LOGOUT
        });
    }
};

export const signup = (first_name, last_name, username, password) => async dispatch => {
    const config = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const body = JSON.stringify({ first_name, last_name, username, password });

    try {
        const res = await axios.post(`${process.env.REACT_APP_BACKEND_API_URL}/api/create-user/`, body, config);
        dispatch({
            type: SIGNUP_SUCCESS,
            payload: res.data
        });
        return {
            signup: true
        }
    } catch (err) {
        dispatch({
            type: SIGNUP_FAIL
        })
        return {
            signup: false,
            err
        }
    }
};