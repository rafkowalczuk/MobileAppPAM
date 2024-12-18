import {StyleSheet, TextInput, View, Platform, Button, Text, TouchableOpacity, ScrollView} from "react-native";
import {ThemedText} from "@/components/ThemedText";
import {useNavigation} from "expo-router";
import React, {useContext, useEffect, useState} from "react";
import {Picker} from '@react-native-picker/picker';
import Modal from 'react-native-modal';
import {IconSymbol} from "@/components/ui/IconSymbol";
import {Tag, Unit} from "@/types/recipe";
import {sendRequest} from "@/utils/fetch";
import {AuthContext} from "@/types/app-auth";

const availableCategories: string[] = [
  'cakes',
  'desserts',
  'dinners',
  'fast-food',
  'breakfast',
  'quick',
  'category n',
  'category n+1',
  'category n+2',
  'category n+3',
  'category n+4',
];

type Ingredient = {
  key: number;
  ingredientId: number | null;
  unitId: number | null;
  amount: string;
}

type Step = {
  key: number;
  value: string;
}

type IngredientL = {
  ingredient: string;
  id: number;
};

type CreateRecipeReq = {
  categories: [string];
  description: string;
  name: string;
  steps: string[];
  tags: never[];
};

export default () => {

  const navigation = useNavigation()

  const {data: authData} =  useContext(AuthContext);

  useEffect(() => {
    navigation.setOptions({headerBackTitle: 'Profile', headerTitle: 'Create recipe'});
  }, []);

  const [recipeName, setRecipeName] = useState('');
  const [description, setDescription] =  useState('');
  const [category, setCategory] = useState<string | null>(null);
  const [tagIds, setTagIds] = useState<number[]>([]);
  const [steps, setSteps] = useState<Step[]>([]);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);

  const [isModalVisible, setModalVisible] = useState(false);
  const [ingredientModalIngredientKey, setIngredientModalIngredientKey] = useState(0);
  const [ingredientModalUnitKey, setIngredientModalUnitKey] = useState(0);

  const [tags, setTags] = useState<Tag[] | undefined | null>();
  const [units, setUnits] = useState<Unit[] | undefined | null>();
  const [ingredientList, setIngredientList] = useState<IngredientL[] | null | undefined>();

  useEffect(() => {
    sendRequest('/unit/all').then(res => {
      if (res.status === 200) {
        setUnits(res.data);
      }
    })

    sendRequest('/ingredient/all').then(res => {
      if (res.status === 200) {
        setIngredientList(res.data);
      }
    })

    sendRequest('/tag/all', {
      headers: {
        // Authorization: authData?.Authorization
      }
    }).then(res => {
      if (res.status === 200 && res.headers["content-type"] === 'application/json') {
        setTags(res.data);
      }
    })
  }, [])

  const addStep = () => {
    const nextKey = Math.max(...steps.map(i => i.key), 1) + 1;
    setSteps([...steps, { key: nextKey, value: '' }]);
  }

  const deleteStep = (key: number) => {
    setSteps(steps.filter(step => step.key !== key))
  }

  const updateStep = (key: number, text: string) => {
    setSteps(steps.map(step => ({
      key: step.key,
      value: step.key === key ? text : step.value
    })))
  }

  const addIngredient = () => {
    const nextKey = Math.max(...ingredients.map(i => i.key), 0) + 1;
    const newList = [...ingredients, { key: nextKey, ingredientId: null, amount: '', unitId: null}];
    setIngredients(newList);
  }

  const deleteIngredient = (key: number) => {
    setIngredients(ingredients.filter(ingredient => ingredient.key !== key))
  }

  const updateIngredientAmount = (key: number, value: string) => {
    setIngredients(ingredients.map(ingredient => ({
      ...ingredient,
      amount: ingredient.key === key ? value : ingredient.amount
    })))
  }

  const updateIngredientIngredient = (key: number, value: string) => {
    const parsed = Number.parseInt(value, 10);
    setIngredients(ingredients.map(ingredient => ({
      ...ingredient,
      ingredientId: ingredient.key === key ? parsed : ingredient.ingredientId
    })))
  }

  const updateIngredientUnit = (key: number, value: string) => {
    const parsed = Number.parseInt(value, 10);
    setIngredients(ingredients.map(ingredient => ({
      ...ingredient,
      unitId: ingredient.key === key ? parsed : ingredient.unitId
    })))
  }

  const updateTags = (tagId: number) => {
    if (tagIds.includes(tagId)) {
      setTagIds(tagIds.filter(tid => tid !== tagId));
    } else {
      setTagIds([...tagIds, tagId]);
    }
  }

  const createRecipe = () => {

    if (category === null) {
      // todo: indicate invalid data somehow
      return;
    }

    const requestData: CreateRecipeReq = {
      steps: steps.map(s => s.value),
      tags: [],
      categories: [category],
      name: recipeName,
      description: description,
    }

    sendRequest('/recipe/new', {
      data: requestData,
      method: 'post',
      headers: {
        Authorization: authData?.Authorization,
      }
    }).then(res => {
      if (res.status === 200 && res.headers['content-type'] === 'application/json') {
        const createdRecipeId = res.data.id;

        tagIds.map(tagId =>
          sendRequest(`recipe/${createdRecipeId}/tags/${tagId}`, {
            method: 'post',
            headers: {
              Authorization: authData?.Authorization
            }
          })
        )

        ingredients.map(ingredient => sendRequest('ingredients/recipes/new', {
          headers: {Authorization: authData?.Authorization},
          method: 'post',
          data: {
            recipeId: createdRecipeId,
            ingredientId: ingredient.ingredientId,
            amount: ingredient.amount,
            unitId: ingredient.unitId,
          }
        }))

      }

    })
  }

  if (units === undefined || ingredientList === undefined || tags === undefined) {
    // todo: add loader
    return null;
  }

  if (units === null || ingredientList === null || tags === null) {
    // todo: it is error state, add message
    return null
  }


  return <ScrollView><View style={{padding: 15, gap: 12}}>

    <ThemedText style={styles.sectionTitleText}>Basic information</ThemedText>

    <ThemedText style={styles.tipInfoText}>
      Name should be short and contain only nouns and adjectives.
    </ThemedText>

    <TextInput placeholder={'Name'} style={styles.textInput} value={recipeName}
               onChange={e => setRecipeName(e.nativeEvent.text)}/>

    <ThemedText style={styles.tipInfoText}>
      Add short but meaningful description of your dish/cake etc. so people can get idea
      what taste they can create.
    </ThemedText>

    <TextInput multiline placeholder={'Description'} style={styles.textInput} value={description}
               onChange={e => setDescription(e.nativeEvent.text)}/>


    <ThemedText style={styles.tipInfoText}>
      Select one or more tags for your recipe.
    </ThemedText>

    <View style={{ display: 'flex', flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>

    { tags.map(tag => (
      <ThemedText key={tag.id} style={tagIds.includes(tag.id) ? {...styles.tag, ...styles.selectedTag} : styles.tag} onPress={() => updateTags(tag.id)}>{tag.tag}</ThemedText>
    )) }

    </View>

    <View style={styles.categoryLabelContainer}>

    <ThemedText style={styles.categoryLabel}>{ category || 'Choose category' }</ThemedText>
    <Button title={category ? 'Change' : 'Select'} onPress={() => setModalVisible(true)} />
    </View>

    <View style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }} >
      <ThemedText style={styles.sectionTitleText}>Steps</ThemedText>

      <TouchableOpacity style={styles.primaryButton} onPress={addStep}>
        <Text style={styles.primaryButtonText}>Add step</Text>
      </TouchableOpacity>
    </View>

    { steps.map((step, stepNo) => (
      <View style={stepStyle.container} key={step.key}>
        <ThemedText>{stepNo + 1}.</ThemedText>
        <TextInput placeholder={'Write here'} style={stepStyle.input} value={step.value} onChangeText={t => updateStep(step.key, t)} />
        <TouchableOpacity style={stepStyle.button} onPress={() => deleteStep(step.key)}>
          <IconSymbol size={18} name="trash.fill" color={'#1c7ed6'} />
        </TouchableOpacity>
      </View>
    )) }


    <View style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }} >
      <ThemedText style={styles.sectionTitleText}>Ingredients</ThemedText>

      <TouchableOpacity style={styles.primaryButton} onPress={addIngredient}>
        <Text style={styles.primaryButtonText}>Add ingredient</Text>
      </TouchableOpacity>
    </View>

    { ingredients.map((ingredient) => (
      <View style={ingredientStyles.container} key={ingredient.key}>

        <View style={{...ingredientStyles.selectorInput, flex: 3}}>
          { /* todo: display valid ingredient instead of id */ }
          { ingredient.ingredientId && (
            <ThemedText style={ingredientStyles.selectorText}>{ingredientList.find(i => i.id === ingredient.ingredientId)?.ingredient}</ThemedText>
          ) }
          <TouchableOpacity style={ingredientStyles.selectorButton} onPress={() => setIngredientModalIngredientKey(ingredient.key)}>
            { !ingredient.ingredientId && (
              <Text style={ingredientStyles.selectorButtonText}>Select ingredient</Text>
            ) }
          </TouchableOpacity>

        </View>

        <TextInput placeholder={'Amount'} keyboardType={'numeric'} style={stepStyle.input} value={ingredient.amount} onChangeText={t => updateIngredientAmount(ingredient.key, t)} />

        <View style={ingredientStyles.selectorInput}>
          { ingredient.unitId && (
            <ThemedText style={ingredientStyles.selectorText}>{units.find(u => u.id === ingredient.unitId)?.unit}</ThemedText>
          ) }
          <TouchableOpacity style={ingredientStyles.selectorButton} onPress={() => setIngredientModalUnitKey(ingredient.key)}>
            { !ingredient.unitId && (
              <Text style={ingredientStyles.selectorButtonText}>Unit</Text>
            ) }
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={stepStyle.button} onPress={() => deleteIngredient(ingredient.key)}>
          <IconSymbol size={18} name="trash.fill" color={'#1c7ed6'} />
        </TouchableOpacity>
      </View>
    )) }

    <View style={{height: 50}} />

    <TouchableOpacity style={{...styles.primaryButton, padding: 10}} onPress={createRecipe}>
      <Text style={{...styles.primaryButtonText, fontSize: 20}}>Create recipe</Text>
    </TouchableOpacity>

    <View style={{height: 150}} />


    <Modal
      isVisible={isModalVisible}
      onBackdropPress={() => setModalVisible(false)}
      style={Platform.OS === 'ios' ? styles.bottomModal : styles.centerModal}
      onModalShow={() => { setCategory( category || 'java') }  }
    >
      <View style={styles.modalContent}>
        <Picker
          selectedValue={category}
          style={{ width: '100%' }}
          onValueChange={(itemValue) => setCategory(itemValue)}
        >
          {availableCategories.map(c => (
            <Picker.Item key={c} label={c} value={c} />
          ))}
        </Picker>
      </View>
    </Modal>

    <Modal
      isVisible={!!ingredientModalIngredientKey}
      onBackdropPress={() => setIngredientModalIngredientKey(0)}
      style={Platform.OS === 'ios' ? styles.bottomModal : styles.centerModal}
    >
      <View style={styles.modalContent}>
        <Picker<string | null>
          selectedValue={ingredients.find(i => i.key === ingredientModalIngredientKey)?.ingredientId?.toString()}
          style={{ width: '100%' }}
          onValueChange={(itemValue) => updateIngredientIngredient( ingredientModalIngredientKey, itemValue!)}
        >
          { ingredientList.map(ingredient => (
            <Picker.Item key={ingredient.id} label={ingredient.ingredient} value={ingredient.id} />
          )) }
        </Picker>
      </View>
    </Modal>

    <Modal
      isVisible={!!ingredientModalUnitKey}
      onBackdropPress={() => setIngredientModalUnitKey(0)}
      style={Platform.OS === 'ios' ? styles.bottomModal : styles.centerModal}
    >
      <View style={styles.modalContent}>
        <Picker<string | null>
          selectedValue={ingredients.find(i => i.key === ingredientModalUnitKey)?.unitId?.toString()}
          style={{ width: '100%' }}
          onValueChange={(itemValue) => updateIngredientUnit( ingredientModalUnitKey, itemValue!)}
        >
          { units.map(unit => (
            <Picker.Item key={unit.id} label={unit.unit} value={unit.id} />
          )) }
        </Picker>
      </View>
    </Modal>

  </View>
  </ScrollView>
}



const styles = StyleSheet.create({
  sectionTitleText: {
    fontSize: 22,
    fontWeight: 'bold'
  },
  textInput: {
    backgroundColor: 'white',
    padding: 10,
    fontSize: 20,
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 0},
    shadowRadius: 4,
    shadowOpacity: 0.2,
  },
  primaryButton: {
    backgroundColor: '#1c7ed6',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
  },
  primaryButtonText: {
    color: 'white',
    fontSize: 16,
  },
  categoryLabelContainer: {
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 0},
    shadowRadius: 4,
    shadowOpacity: 0.2,
    display: 'flex',
    backgroundColor: 'white',
    paddingVertical: 4,
    paddingHorizontal: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  categoryLabel: {
    fontSize: 18,


  },
  tag: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    backgroundColor: 'white',
    borderRadius: 20,
  },
  selectedTag: {
    backgroundColor: '#1c7ed6',
    color: 'white',
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: 10,
    alignItems: 'center',
  },
  bottomModal: {
    justifyContent: 'flex-end',
    margin: 0,
  },
  centerModal: {
    justifyContent: 'center',
    margin: 0,
  },
  tipInfoText: {fontSize: 11, color: '#887', lineHeight: 13}
});

const stepStyle = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 0},
    shadowRadius: 4,
    shadowOpacity: 0.2,
    padding: 4,
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingLeft: 8,
    paddingRight: 5,
  },
  input: {
    fontSize: 18,
    height: 36,
    flex: 1,
    paddingHorizontal: 8,
    borderColor: '#ddd',
    borderWidth: 1,
  },
  button: {
    display: 'flex',
    justifyContent: 'center',
    height: 36,
    backgroundColor: '#e8f3fc',
    paddingHorizontal: 12,
    paddingVertical: 7,
  }
})

const ingredientStyles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 0},
    shadowRadius: 4,
    shadowOpacity: 0.2,
    padding: 4,
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingHorizontal: 5,
  },
  selectorInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    paddingHorizontal: 8,
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 8,
  },
  selectorButton: {
  },
  selectorButtonText: {
    fontSize: 18,
    color: 'blue'
  },
  selectorText: {
    fontSize: 18,
    lineHeight: 22,
  },

});
