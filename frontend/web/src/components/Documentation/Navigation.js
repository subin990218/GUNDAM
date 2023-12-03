import styles from './Navigation.module.css';

const Navigation = ({ methods }) => {
  const scrollToMethod = (id) => {
    document.getElementById(id).scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <nav>
      <ul>
        {methods.map((method, index) => (
          <li key={index} className={styles.li} onClick={() => scrollToMethod(`method-${index}`)}>
            {method.name}
          </li>
        ))}
      </ul>
    </nav>
  );
};

  export default Navigation;