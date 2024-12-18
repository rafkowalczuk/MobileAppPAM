import React, {FC, useEffect, useState} from "react";
import {useLocalSearchParams, useNavigation, useRouter} from "expo-router";
import {ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View} from "react-native";
import {ThemedText} from "@/components/ThemedText";
import {sendRequest} from "@/utils/fetch";

const WriteComment: FC = () => {

  // todo: button should be disabled if comment is empty, during request processing and after successful
  //  request processing (comment created)

  const {id} = useLocalSearchParams<{ id: string }>();


  const navigation = useNavigation();
  const {back} = useRouter();

  useEffect(() => {
    navigation.setOptions({headerBackTitle: 'Recipe', headerTitle: 'Write comment'});
  }, []);

  const [comment, setComment] = useState('');

  const createComment = async () => {

    if (comment.trim().length === 0) {
      return;
      // todo: notify user
    }

    const response = await sendRequest(
      'comment/new',
      {
        method: 'POST',
        data: {
          comment,
          recipeId: Number.parseInt(id, 10),
        },
        headers: {
          Authorization: 'Basic a3J6eXN6dG9mbWVsZXJAdnAucGw6cGFzc3dvcmQ=', // todo: use real auth
        }
      }
    );

    if (response.status === 200) {
      // ok, comment created
      setTimeout(back, 600);
    }

    // todo: add error handling
  }

  return (
    <ScrollView>
      <View style={{padding: 15, gap: 12}}>
        <TextInput placeholder={'Your comment here'} multiline style={styles.textInput} value={comment}
                   onChange={e => setComment(e.nativeEvent.text)}/>

        <ThemedText style={{fontSize: 11, color: '#887', lineHeight: 13}}>
          Please remember to be kind and avoid insulting other people. Comments may be removed if you break
          rules described in our terms of service. Your IP is stored in our systems together with your email.
          Only your email address is shared next to your comment.
        </ThemedText>

        <TouchableOpacity style={styles.writeCommentButton} onPress={() => createComment()}>
          <Text style={styles.writeCommentButtonText}>Send comment</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}


const styles = StyleSheet.create({
  textInput: {
    backgroundColor: 'white',
    padding: 10,
    fontSize: 20,
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 0},
    shadowRadius: 4,
    shadowOpacity: 0.2,
  },
  writeCommentButton: {
    marginLeft: 'auto',
    backgroundColor: 'rgba(34, 139, 230, 0.1)',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 3,
    width: 170,
  },
  writeCommentButtonText: {
    color: '#1c7ed6',
    fontSize: 16,
    fontWeight: 'bold'
  },
});


export default WriteComment;
