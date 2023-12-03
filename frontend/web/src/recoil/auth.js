import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';


const { persistAtom } = recoilPersist({
  key: 'accessToken', 
  storage: localStorage,  
});

export const accessTokenAtom = atom({
  key: 'accessTokenAtom',
  default: null,
  effects_UNSTABLE: [persistAtom],
});
