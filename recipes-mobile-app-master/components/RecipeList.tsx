import React, {FC, useEffect, useState} from 'react';
import {View, Text, Button, StyleSheet, FlatList, TouchableOpacity} from 'react-native';
import {useRouter} from "expo-router";
import {sendRequest} from "@/utils/fetch";
import {Recipe} from "@/types/recipe";

const styles = StyleSheet.create({
  tile: {
    backgroundColor: '#fff',
    padding: 16,
    marginVertical: 8,
    marginHorizontal: 16,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 2,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  description: {
    fontSize: 14,
    marginVertical: 4,
  },
  smallText: {
    fontSize: 12,
    color: 'gray',
  },
  button: {
    backgroundColor: 'rgba(34, 139, 230, 0.1)',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
    marginTop: 10,
    width: 120,
  },
  buttonText: {
    color: '#1c7ed6',
    fontSize: 16,
    fontWeight: 'bold'
  },
});


type TileProps = {
  id: number;
  name: string;
  description: string;
  category: string;
  stepsCount: number;
}

const Tile: FC<TileProps> = ({id, name, description, category, stepsCount }) => {
  const {push} = useRouter()



  return (
    <View style={styles.tile}>
      <Text style={styles.title}>{name}</Text>
      <Text style={styles.description}>{description}</Text>
      <Text style={styles.smallText}>{stepsCount} steps | Category: {category}</Text>
      <TouchableOpacity style={styles.button} onPress={() => push({ pathname: '/recipe/[id]', params: { id } })}>
        <Text style={styles.buttonText}>Show</Text>
      </TouchableOpacity>
    </View>
  );
}

const RecipeList: FC = () => {


  const [recipes, setRecipes] = useState<Recipe[] | null>(null);

  useEffect(() => {
    sendRequest('/recipe/all').then(res => {
      if (res.status === 200 && res.headers['content-type'] === 'application/json') {
        setRecipes(res.data);
      }
      // todo: handle fetch error
    })
  }, []);

  return (
    <View>
      {recipes && recipes.map(recipe => (
        <Tile key={recipe.id} name={recipe.name} description={recipe.description} category={recipe.categories[0]} stepsCount={recipe.steps.length} id={recipe.id} />
      ))}
    </View>
  );
}

export { RecipeList }
