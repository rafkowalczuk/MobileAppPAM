import React, {FC, useContext, useState} from "react";
import {Button, SafeAreaView, StyleSheet, Text, TextInput, TouchableOpacity, View} from "react-native";
import {sendRequest} from "@/utils/fetch";
import {AuthContext, Role} from "@/types/app-auth";
import {getBearer} from "@/utils/auth";
import {ThemedText} from "@/components/ThemedText";
import {useRouter} from "expo-router";

type SignUpPageProps = {
  signIn: () => void;
}

const SignUpPage: FC<SignUpPageProps> = ({ signIn }) => {
  const {push, replace} = useRouter()

  const { update: setAuthData } = useContext(AuthContext);


  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const signUp = async () => {
    const response = await sendRequest('register', {
      data: {
        email,
        password,
        roles: [Role.USER, Role.ADMIN],
      },
      method: 'POST'
    });

    if (response.status === 200) {
      setAuthData({
        password,
        email,
        roles: response.data.roles,
        Authorization: getBearer(email, password),
      });

      setTimeout(() => {
        replace('/(tabs)');
      }, 700);
    } else if (response.status === 400) {
      // todo: handle it
      console.error(response.statusText)
    }
  };

  // todo: add terms and conditions checkbox

  return (
    <SafeAreaView style={{ backgroundColor: '#eee', flex: 1 }}>
      <View style={styles.formContainer}>
        <ThemedText style={styles.titleText}>
          Sign up
        </ThemedText>


        <View style={{ height: 20 }} />

        <ThemedText style={styles.labelText}>E-mail</ThemedText>
        <TextInput style={styles.input} value={email} onChange={(e) => setEmail(e.nativeEvent.text)} />

        <View style={{ height: 14 }} />


        <ThemedText style={styles.labelText}>Password</ThemedText>
        <TextInput
          style={styles.input}
          value={password}
          onChange={(e) => setPassword(e.nativeEvent.text)}
        />

        <View style={{ height: 14 }} />

        <ThemedText style={styles.termsText}>By signing up you confirm that you had read and agree to our terms of service.</ThemedText>

        <TouchableOpacity style={styles.signUpButton} onPress={(_) => signUp()}>
          <Text style={styles.signUnButtonText}>Create account</Text>
        </TouchableOpacity>

        <ThemedText style={{ margin: 'auto', marginTop: 10, color: '#999', fontSize: 18 }}>
          or
        </ThemedText>

        <TouchableOpacity style={styles.signInButton} onPress={signIn}>
          <Text style={styles.signInButtonText}>Sign in with existing account</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  )
}


const styles = StyleSheet.create({
  formContainer: {
    backgroundColor: '#fff',
    paddingVertical: 22,
    paddingHorizontal: 16,
    margin: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 2,
  },
  titleText: {
    fontSize: 22,
    fontWeight: 'bold',
  },
  labelText: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 4,
    paddingLeft: 5,
  },
  input: {
    fontSize: 20,
    padding: 10,
    borderColor: '#ccc',
    borderWidth: 1,
  },
  termsText: {
    color: '#555',
    fontSize: 13,
    lineHeight: 20,
  },
  signUpButton: {
    backgroundColor: '#1c7ed6',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 10,
  },
  signUnButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold'
  },
  signInButton: {
    backgroundColor: 'rgba(34, 139, 230, 0.1)',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 10,
  },
  signInButtonText: {
    color: '#1c7ed6',
    fontSize: 16,
    fontWeight: 'bold'
  },
});

export default SignUpPage;
