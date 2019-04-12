<#-- @ftlvariable name="messages" type="kotlin.collections.AbstractList<java.lang.String>" -->
<#-- @ftlvariable name="cart" type="io.github.lmller.catfood.configurator.Cart" -->
<#-- @ftlvariable name="items" type="kotlin.collections.AbstractList<io.github.lmller.catfood.configurator.Item>" -->
<html>
<h2>Cat food configurator</h2>
<div>
    <ul>
    <#list messages as msg>
        <li>${msg}</li>
    </#list>
    </ul>
</div>

<div style="width: 50%">
    <div style="border: 1px solid green; padding: 22px; float: left; width: 65%">
        <h3>Items</h3>
        <ul>
            <#list items as item>
                <li>${item.name} - ${item.price}
                    <form method="post" action="/item">
                        <input type="hidden" name="itemName" value="${item.name}">
                        <button type="submit" name="add" formmethod="post">add to cart</button>
                    </form>
                </li>
            </#list>
        </ul>
    </div>

    <div style="border: 1px solid darkred; padding: 22px; float: right">
        <h3>Cart</h3>
        <ul>
            <#list cart.items as item>
                <li>${item.name} - ${item.price}</li>
            </#list>
        </ul>

        Total: <b>${cart.totalAmount}</b>
        <form method="post" action="/checkout">
            <button name="checkout" type="submit" formmethod="post">check out!</button>
        </form>
    </div>
</div>
</html>