import React, {FC, useContext} from "react";
import {SafeAreaView, StyleSheet, Text, TouchableOpacity, View} from "react-native";
import {AuthContext, Role} from "@/types/app-auth";
import {ThemedText} from "@/components/ThemedText";
import {useRouter} from "expo-router";
import {IconSymbol} from "@/components/ui/IconSymbol";

const ProfilePage: FC = () => {

  const { data: authData, update: updateAuth } = useContext(AuthContext);
  const { replace, push } = useRouter()
  const logout = () => {
    setTimeout(() => {
      updateAuth(null);
      replace('/(tabs)');
    }, 200)
  }

  if (authData === null) {
    throw Error('Invalid component mounted - auth data not provided so profile cannot be displayed');
  }

  const isAdmin = authData.roles.includes(Role.ADMIN);

  return <SafeAreaView>

    <View style={{ padding: 15, gap: 10 }}>
      <View style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
        <ThemedText style={styles.titleText}>
          My profile
        </ThemedText>

        <TouchableOpacity style={{...styles.mainButton, width: 100}} onPress={logout}>
          <Text style={styles.mainButtonText}>Logout</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.infoContainer}>

        <IconSymbol size={44} name="person" color={'black'} />
        <ThemedText style={{ fontSize: 18 }}>
          { authData.email }
        </ThemedText>

        <ThemedText style={{ fontSize: 14, fontWeight: 'bold', backgroundColor: isAdmin ? '#e03131' : '#1a8f00', color: 'white', paddingVertical: 4, paddingHorizontal: 10, borderRadius: 20 }}>
          { isAdmin ? 'ADMIN' : 'USER' }
        </ThemedText>
      </View>

      <TouchableOpacity style={{...styles.mainButton, width: 145}} onPress={() => push('/recipe/create')}>
        <Text style={styles.mainButtonText}>Create recipe</Text>
      </TouchableOpacity>

      { isAdmin && (
        <TouchableOpacity style={{...styles.adminButton, width: 220}} onPress={() => ({})}>
          <Text style={styles.adminButtonText}>Administrative functions</Text>
        </TouchableOpacity>
      ) }

    </View>



  </SafeAreaView>

}



const styles = StyleSheet.create({
  infoContainer: {
    backgroundColor: '#fff',
    paddingVertical: 22,
    paddingHorizontal: 16,
    shadowColor: '#000',
    marginVertical: 13,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 2,
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  titleText: {
    fontSize: 28,
    lineHeight: 40,
    fontWeight: 'bold',
  },
  mainButton: {
    backgroundColor: '#1c7ed6',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
  },
  mainButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold'
  },
  adminButton: {
    backgroundColor: '#e03131',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
  },
  adminButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold'
  },
});


export { ProfilePage }
