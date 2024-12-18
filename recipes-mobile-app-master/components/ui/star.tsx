import {FC} from "react";
import {Image, View} from "react-native";

import GoldStar from '@/assets/images/star-gold.svg';
import GrayStar from '@/assets/images/star-gray.svg';

type SingleStarIconProps = {
  color: 'gray' | 'gold',
  size?: number,
}

const SingleStarIcon: FC<SingleStarIconProps> = ({color, size = 12}) => {
  switch (color) {
    case 'gold':
      return <GoldStar width={size} height={size} />
    case 'gray':
      return <GrayStar width={size} height={size} />
  }

  return null;
}

type StarSetProps = {
  count: number;
  value: number;
  size: number;
}

const StarSet: FC<StarSetProps> = ({count, value, size}) => (
  <View style={{ display: 'flex', flexDirection: 'row' }}>
    {Array.from({length: count}).map((_, index) => (
      <SingleStarIcon color={value > index ? 'gold' : 'gray'} size={size} key={index} />
    ))}
  </View>
)


export { SingleStarIcon, StarSet }
