type Recipe = {
  id: number;
  name: string;
  categories: string[];
  description: string;
  steps: string[];
  tags: string[];
};

type User = {
  // id: number;
  email: string;
  // password: number;
  // roles: Role[];
};

type Comment = {
  id: number;
  // recipe: RecipeWithOwner;
  user: User;
  date: string;
  comment: string;
};

type Rating = {
  id: number;
  // recipe: RecipeWithOwner;
  user: User;
  rating: number;
  date: string;
};

type Unit = {
  unit: string;
  id: number;
};

type Tag = {
  tag: string;
  id: number;
};

export { Recipe, User, Comment, Rating, Unit, Tag }
