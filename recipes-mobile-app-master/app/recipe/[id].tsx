import React, {FC, useEffect, useState} from "react";
import {StyleSheet, Text, View, Image, ScrollView, FlatList, TextInput, Button, TouchableOpacity} from "react-native";
import {useLocalSearchParams, useNavigation, useRouter} from "expo-router";
import {ThemedText} from "@/components/ThemedText";
import {StarSet} from "@/components/ui/star";
import {Comment, Rating, Recipe} from "@/types/recipe";
import {sendRequest} from "@/utils/fetch";

const RecipeScreen: FC = () => {

  const navigation = useNavigation()

  useEffect(() => {
    navigation.setOptions({headerBackTitle: 'List', headerTitle: 'Recipe'});
  }, []);

  // todo: rating feature
  const { id } = useLocalSearchParams<{id: string}>();


  // null/undefined usage description: undefined -> loading, null -> error occurred, actual value -> data fetched successfully
  const [recipe, setRecipe] = useState<Recipe | null | undefined>(undefined);
  const [ratings, setRatings] = useState<Rating[] | null | undefined>(undefined);
  const [comments, setComments] = useState<Comment[] | null | undefined>(undefined);

  useEffect(() => {
    (async () => {
      sendRequest('/recipe/' + id, {
        headers: {
          Authorization: 'Basic a3J6eXN6dG9mbWVsZXJAdnAucGw6cGFzc3dvcmQ=', // todo: use real auth
        }
      }).then(res => {
        if (res.status === 200 && res.headers['content-type'] === 'application/json') {
          setRecipe(res.data);
        }
      })


      sendRequest(`rating/search/?recipeId=${id}`, {
        headers: {
          Authorization: 'Basic a3J6eXN6dG9mbWVsZXJAdnAucGw6cGFzc3dvcmQ=', // todo: use real auth
        }
      }).then(res => {
        if (res.status === 200 && res.headers['content-type'] === 'application/json') {
          setRatings(res.data);
        }
      })

      sendRequest(`comment/search/?recipeId=${id}`, {
        headers: {
          Authorization: 'Basic a3J6eXN6dG9mbWVsZXJAdnAucGw6cGFzc3dvcmQ=', // todo: use real auth
        }
      }).then(res => {
        if (res.status === 200 && res.headers['content-type'] === 'application/json') {
          setComments(res.data);
        }
      })
    })()
  }, [id])



  const {push} = useRouter()

  if (recipe === null || ratings === null || comments === null) {
    // todo: add not found error screen
    return null;
  }

  if (recipe === undefined || ratings === undefined || comments === undefined) {
    // todo: add loader
    return null;
  }

  const avgScore = (ratings.map((r) => r.rating).reduce((all, curr) => all + curr, 0) / ratings.length) || 0;

  return (
    <ScrollView style={styles.container}>
      <ThemedText style={styles.categoryText}>{recipe.categories[0]}</ThemedText>
      <ThemedText style={styles.nameText} type="title">{recipe.name}</ThemedText>

      <View style={{flexDirection: 'row', display: 'flex', justifyContent: 'flex-start', gap: 10, alignContent: 'flex-end', marginVertical: 3}}>
        <StarSet count={5} value={avgScore} size={18} />
        <ThemedText style={{color: '#666', fontSize: 13}}>{avgScore} from ({ratings.length}) ratings</ThemedText>
      </View>

      <ThemedText style={styles.descriptionText}>{recipe.description}</ThemedText>

      <ThemedText>
        Steps:
      </ThemedText>
      { recipe.steps.map((step, index) => (
        <ThemedText key={index}>{index + 1}. {step}</ThemedText>
      )) }

      <View style={{height: 20}} />

      <ThemedText style={styles.nameText} type="title">Comments</ThemedText>

      <View style={{height: 10}} />

      <View style={{ flex: 1, gap: 8, }}>
        { comments.length === 0 && <View style={styles.emptyTile}><Text>No comments yet</Text></View> }

        { comments.map(comment =>
          <View style={styles.tileContainer} key={comment.id}>
            <View style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', marginBottom: 4 }}>
              <Text style={styles.userNameText}>{comment.user.email}</Text>
              <Text style={styles.dateText}>{comment.date}</Text>
            </View>
            <Text style={styles.commentContentText}>{comment.comment}</Text>
          </View>
        ) }

        <TouchableOpacity style={styles.writeCommentButton} onPress={() => push({ pathname: '/recipe/[id]/write-comment', params: { id } })}>
          <Text style={styles.writeCommentButtonText}>Write your own comment</Text>
        </TouchableOpacity>

      </View>


      <View style={{height: 20}} />

      <ThemedText style={styles.nameText} type="title">Ratings</ThemedText>

      <View style={{height: 10}} />


      <View style={{ flex: 1, gap: 8, }}>
        { ratings.length === 0 && <View style={styles.emptyTile}><Text>No ratings yet</Text></View> }

        { ratings.map(rating =>
          <View style={{...styles.tileContainer, display: 'flex', flexDirection: 'row', justifyContent: 'space-between', marginBottom: 4, alignItems: 'center' }} key={rating.id}>
            <StarSet count={5} value={rating.rating} size={12} />
            <Text style={styles.userNameText}>{rating.user.email}</Text>
            {/*<Text style={styles.dateText}>{rating.date}</Text>*/}
          </View>
        ) }

      </View>

      <View style={{height: 30}} />


    </ScrollView>

  )
}

const styles = StyleSheet.create({
  container: {
    padding: 15,
  },
  categoryText: {
    fontSize: 13,
    color: '#777'
  },
  nameText: {
    fontSize: 22,
    fontWeight: 'bold',
  },
  descriptionText: {
    fontSize: 16,
    color: '#333',
    marginVertical: 3,
  },
  tileContainer: {
    backgroundColor: 'white',
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 0 },
    shadowRadius: 4,
    shadowOpacity: 0.2,
    padding: 10,
    borderRadius: 5,
  },
  emptyTile: {
    backgroundColor: 'white',
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 0 },
    shadowRadius: 4,
    shadowOpacity: 0.2,
    padding: 10,
    borderRadius: 5,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  userNameText: {
    fontSize: 12,
    color: '#444'
  },
  dateText: {
    fontSize: 12,
    color: '#666'
  },
  commentContentText: {
    fontSize: 14,
    color: '#222',
  },
  writeCommentButton: {
    marginHorizontal: 'auto',
    backgroundColor: 'rgba(34, 139, 230, 0.1)',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 3,
    width: 200,
  },
  writeCommentButtonText: {
    color: '#1c7ed6',
    fontSize: 16,
    fontWeight: 'bold'
  },
});


export default RecipeScreen;
