package ir.food.operatorAndroid.app

class CartDataHolder {

    companion object {
        private var instance: CartDataHolder? = null
        public fun getInstance(): CartDataHolder {
            if (instance == null) {
                instance = CartDataHolder()
            }
            return instance as CartDataHolder
        }
    }

    private var quantity: Int = 0

    var _quantity: Int
        get() = this.quantity
        set(_quantity) {
            this.quantity = _quantity
        }

}