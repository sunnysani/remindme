import logo from './logo.svg';
import './App.css';
import { fetch_user } from './util/prerender';
import { Provider } from 'react-redux';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './components/Home/Home'
import Navbar from './components/Navbar/Navbar';
import SignUp from './pages/SignUp/SignUp';
import Login from './pages/Login/Login';
import store from './store';
import { useState } from 'react';

function App() {
  const [loading, setLoading] = useState(true);
  if (loading) fetch_user(store, setLoading);
  if (loading) return <p>Loading...</p>;
  return (
    <Provider store={store}>
      <Router>
        <div className='App'>
          <Navbar/>
          <div className='content'>
            <Routes>
              <Route path='/' element={<Home/>} />
              <Route path='/signup' element={<SignUp/>} />
              <Route path='/login' element={<Login/>} />
            </Routes>
          </div>
        </div>
      </Router>
    </Provider>
  );
}

export default App;
