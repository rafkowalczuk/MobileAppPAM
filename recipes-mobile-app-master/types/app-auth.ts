import { createContext } from 'react';

enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

type AuthData = {
  email: string;
  password: string;
  roles: Role[];
  Authorization: string;
};

type AuthContextType = {
  data: AuthData | null;
  update: (authData: AuthData | null) => void;
};

const AuthContext = createContext<AuthContextType>({
  data: null,
  update: () => {},
});

const authStorageKeys: Record<keyof AuthData, string> = {
  email: 'tab.credentials.email',
  password: 'tab.credentials.password',
  roles: 'tab.credentials.roles',
  Authorization: 'tab.credentials.Authorization',
};

export { AuthContext, Role, authStorageKeys };
export type { AuthData, AuthContextType };
