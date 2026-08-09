
public double area() {
        return (width * height) / 2.0;
    }
    // equilateral cumple con la funcion que el area del triangulo sea la misma y que sea equilatero
    public void equilateral () {
        // Se calcula el lado para que el area sea la misma, por eso se usa sqrt(2 * area())
        int lado = (int) Math.sqrt(2.0 * area());
        erase();
        width = lado;
        height =lado;
        draw();
    }
    // Metodo walk se usa para dezplazar horizontalmente el objeto Triangle
    public void walk (int times) {
        // se usa Math.abs para obtener el valor absoluto de times
        for (int i = 0; i < Math.abs(times); i++) {
            if (times > 0 ){
                moveHorizontal (20);
            } else {
                moveHorizontal (-20);
            }
        }
    }
    // Se crea agrega otro metodo contructor, este pide pide la altura, anchura y color del triangulo a crear
    public Triangle(String color, int width, int height) {
        //this. se usa para diferenciar el atributo  de la clase del parametro del constructor
        this.color = color;
        this.width = width;
        this.height = height;
        this.xPosition = 140;
        this.yPosition = 15;
        this.isVisible = false;
        }
    // walk2 Se usa para dezplazar el objeto verticalmente 
    public void walk2 (int times) {
        // la funcion de walk2 sera mover hacia arriba o abajo
        for (int i = 0; i < Math.abs (times); i++){
            if (times > 0) {
                moveVertical (-20);
            } else {
                moveVertical (20);
            } 
        }
    }