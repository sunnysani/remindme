import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import rootReducer from './reducer';

// Initial state declared in reducer
const initialState = {};

const middleware = [thunk];

// Store
const store = createStore(
  rootReducer,
  initialState,
  applyMiddleware(...middleware)
);

export default store;
