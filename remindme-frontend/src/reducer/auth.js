//auth.js
import {
    LOGIN_SUCCESS,
    LOGIN_FAIL,
    USER_LOADED_SUCCESS,
    USER_LOADED_FAIL,
    SIGNUP_SUCCESS,
    SIGNUP_FAIL,
    LOGOUT
  } from '../actions/types';

const initialState = {
    access: localStorage.getItem('access'),
    refresh: localStorage.getItem('refresh'),
    isAuthenticated: null,
    user: null
};

function reducer(state = initialState, action) {
    const { type, payload } = action;

    switch (type) {
        case LOGIN_SUCCESS:
            console.log("masuk reducer login sukses")
            localStorage.setItem('access', payload.access);
            localStorage.setItem('refresh', payload.refresh);
            return{
                ...state,
                isAuthenticated:true,
                access : payload.access,
                refresh : payload.refresh
            }
        case SIGNUP_SUCCESS:
            return {
                ...state,
                isAuthenticated: false
            }
        case USER_LOADED_SUCCESS:
            return {
                ...state,
                isAuthenticated: true,
                user: payload
            }
        case LOGIN_FAIL:
            localStorage.removeItem('access');
            localStorage.removeItem('refresh');
            return {
                ...state,
                access: null,
                refresh: null,
                isAuthenticated: false,
                user: null
            }
        case USER_LOADED_FAIL:
            console.log("masuk user loaded fail")
            return {
                ...state,
                user: null
            }
        case SIGNUP_FAIL:
        case LOGOUT:
            localStorage.removeItem('access');
            localStorage.removeItem('refresh');
            return {
                access: null,
                refresh: null,
                isAuthenticated: false,
                user: null
            }
        default:
            return state
    }
}

export default reducer;
