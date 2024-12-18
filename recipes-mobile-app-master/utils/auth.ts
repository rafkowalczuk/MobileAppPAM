const getBearer = (email: string, password: string) => {
  return `Basic ${btoa(`${email}:${password}`)}`;
}

export { getBearer }
