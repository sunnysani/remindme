import { load_user } from '../actions/auth';

export const fetch_user = async (store, setLoading) => {
  if (localStorage.getItem('access')) {
    await load_user()(store.dispatch);
    console.log("masuk sini ces")
  }
  setLoading(false);
}
