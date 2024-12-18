import React, {FC, useContext, useState} from "react";
import {Button, SafeAreaView, StyleSheet, Text, TextInput, TouchableOpacity, View} from "react-native";
import {sendRequest} from "@/utils/fetch";
import {AuthContext, Role} from "@/types/app-auth";
import {getBearer} from "@/utils/auth";
import {ThemedText} from "@/components/ThemedText";
import {useRouter} from "expo-router";


type SignInPageProps = {
  signUp: () => void;
}

const SignInPage: FC<SignInPageProps> = ({ signUp }) => {
  const {push, replace} = useRouter()

  const { update: setAuthData } = useContext(AuthContext);


  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const signIn = async () => {
    console.log('click to sign in');

    sendRequest('http://192.168.1.220:8881/user', {
      method: 'GET',
      fetchOptions: {
      },
      headers: {
        Authorization: getBearer(email, password),
      }
    }).then(res => {
        if (res.headers['content-type'] === 'text/html;charset=UTF-8') {
          // todo: invalid credentials - handle it
          console.log('invalid credentials');
        } else if (res.status === 200 && res.headers['content-type'] === 'application/json') {
          console.log('ok');


          setTimeout(() => {
            setAuthData({
              password,
              email,
              roles: res.data.roles,
              Authorization: getBearer(email, password),
            });

            replace('/(tabs)');
          }, 400);
        }
      }).catch(err => {
        console.log(err);

        if (err instanceof TypeError) {
          console.log(err.message, err.name, err.stack, err.cause)
        }

      }).finally(() => {
        console.log('done');
      })

  };

  return (
    <SafeAreaView style={{ backgroundColor: '#eee', flex: 1 }}>
      <View style={styles.formContainer}>
        <ThemedText style={styles.titleText}>
          Sign in
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

        <TouchableOpacity style={styles.mainButton} onPress={(_) => signIn()}>
          <Text style={styles.mainButtonText}>Sign in</Text>
        </TouchableOpacity>

        <ThemedText style={{ margin: 'auto', marginTop: 10, color: '#999', fontSize: 18 }}>
          or
        </ThemedText>

        <TouchableOpacity style={styles.secondButton} onPress={signUp}>
          <Text style={styles.secondButtonText}>Create new account</Text>
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
  mainButton: {
    backgroundColor: '#1c7ed6',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 10,
  },
  mainButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold'
  },
  secondButton: {
    backgroundColor: 'rgba(34, 139, 230, 0.1)',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 10,
  },
  secondButtonText: {
    color: '#1c7ed6',
    fontSize: 16,
    fontWeight: 'bold'
  },
});

export default SignInPage;
