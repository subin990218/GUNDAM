import { selector } from 'recoil';
import { accessTokenAtom } from '../recoil/auth';
import { userIdAtom, userNameAtom, userImageURLAtom } from '../recoil/user';

export const githubUserQuery = selector({
  key: 'githubUserQuery',
  get: async ({ get }) => {
    const accessToken = get(accessTokenAtom);

    if (!accessToken) {
      return null; 
    }

    try {
      const response = await fetch('https://api.github.com/user', {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      if (!response.ok) {
        throw new Error('github api 호출 실패!!!');
      }

      const data = await response.json();
      return {
        username: data.login, 
        avatarUrl: data.avatar_url, 
        email: data.email
      };
    } catch (error) {
      throw error; 
    }
  },
});
