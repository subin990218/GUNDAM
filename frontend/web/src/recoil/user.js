import { atom } from 'recoil';
import { recoilPersist } from 'recoil-persist';

const { persistAtom } = recoilPersist({
  key: 'userState', 
  storage: localStorage, 
});

export const userIdAtom = atom({
  key: 'userId',
  default: '', 
  effects_UNSTABLE: [persistAtom],
});

export const userNameAtom = atom({
  key: 'userName',
  default: '',
  effects_UNSTABLE: [persistAtom],
});

export const userImageURLAtom = atom({
  key: 'userImageURL',
  default: '',
  effects_UNSTABLE: [persistAtom],
});
