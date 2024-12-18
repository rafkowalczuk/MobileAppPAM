import {FC, useContext, useState} from "react";
import {AuthContext} from "@/types/app-auth";
import SignUpPage from "@/components/SignUpPage";
import SignInPage from "@/components/SignInPage";
import {ProfilePage} from "@/components/ProfilePage";

const UserZone: FC = () => {

  const {data: authData} = useContext(AuthContext);

  const [authScreen, setAuthScreen] = useState<'sign-in' | 'sign-up'>('sign-up');

  if (authData) {
    return <ProfilePage/>
  }

  if (authScreen === 'sign-up') {
    return <SignUpPage signIn={() => setAuthScreen('sign-in')}/>
  } else {
    return <SignInPage signUp={() => setAuthScreen('sign-up')}/>
  }
}

export default UserZone;
