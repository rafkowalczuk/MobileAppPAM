import {DarkTheme, DefaultTheme, ThemeProvider} from '@react-navigation/native';
import {useFonts} from 'expo-font';
import {Stack} from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import {StatusBar} from 'expo-status-bar';
import {useEffect, useMemo, useState} from 'react';
import * as SecureStore from 'expo-secure-store';

import 'react-native-reanimated';

import {useColorScheme} from '@/hooks/useColorScheme';
import {AuthContext, AuthData, authStorageKeys, Role} from "@/types/app-auth";


// todo: check if auth-related things should be there

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const [loaded] = useFonts({
    SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
  });

  useEffect(() => {
    if (loaded) {
      SplashScreen.hideAsync();
    }
  }, [loaded]);

  let storedAuthData: AuthData | null = null;

  {
    const email = SecureStore.getItem(authStorageKeys.email);
    const password = SecureStore.getItem(authStorageKeys.password);
    const roles = SecureStore.getItem(authStorageKeys.roles);
    const authorization = SecureStore.getItem(authStorageKeys.Authorization);

    if (email && password && roles && authorization) {
      storedAuthData = {
        email,
        password,
        roles: roles.split(',') as Role[],
        Authorization: authorization,
      }
    }
  }

  const authDataUpdate = useMemo(
    () => async (data: AuthData | null) => {
      if (data) {
        SecureStore.setItem(authStorageKeys.email, data.email);
        SecureStore.setItem(authStorageKeys.password, data.password);
        SecureStore.setItem(authStorageKeys.Authorization, data.Authorization);
        SecureStore.setItem(authStorageKeys.roles, data.roles.join(','));
      } else {
        await Promise.all([
          SecureStore.deleteItemAsync(authStorageKeys.email),
          SecureStore.deleteItemAsync(authStorageKeys.password),
          SecureStore.deleteItemAsync(authStorageKeys.Authorization),
          SecureStore.deleteItemAsync(authStorageKeys.roles),
        ])
      }

      setAuthData(data);
    },
    [],
  );


  const [authData, setAuthData] = useState<AuthData | null>(storedAuthData);
  const authContextValue = useMemo(() => ({data: authData, update: authDataUpdate}), [authData]);


  if (!loaded) {
    return null;
  }

  return (
    <AuthContext.Provider value={authContextValue}>
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      <Stack>
        <Stack.Screen name="(tabs)" options={{headerShown: false}}/>
        <Stack.Screen name="/recipe/create" options={{headerShown: true}}/>
        <Stack.Screen name="/recipe/[id]" options={{headerShown: true}}/>
        <Stack.Screen name="/recipe/[id]/write-comment" options={{headerShown: true}}/>
        <Stack.Screen name="+not-found"/>
      </Stack>
      <StatusBar style="auto"/>
    </ThemeProvider>
    </AuthContext.Provider>
  );
}
