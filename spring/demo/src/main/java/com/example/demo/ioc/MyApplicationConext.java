package com.example.demo.ioc;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationConext {

    //存放所有类的集合
    private List<Class<?>> classList = new ArrayList<>();

    //定义存放类的实例对象的集合
    private Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    public MyApplicationConext() {

    }

    public MyApplicationConext(String basePackage) {
        scan(basePackage);
        doAutoInject();
    }

    private void scan(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File file = new File(resource.getPath());
        File[] files = file.listFiles(f -> {
            if (f.isDirectory()) {
                scan(basePackage + "." + f.getName());
            } else {
                if (f.getName().endsWith(".class")) {
                    String classPath = basePackage + "." + f.getName().replaceAll("\\.class", "");
                    Class<?> clz = null;
                    try {
                        clz = Class.forName(classPath);
                        //判断是否是有@Component注解的类
                        if (clz.isAnnotationPresent(MyComponent.class)) {
                            classList.add(clz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        });
    }

    private void doInstance() {
        classList.forEach(c -> {
            try {
                Object o = c.newInstance();
                String key = c.getSimpleName().substring(0, 1).toLowerCase() + c.getSimpleName().substring(1);
                if (!instanceMap.containsKey(key)) {
                    instanceMap.put(key, o);
                }
                //判断当前类是否实现了接口
                Class<?>[] interfaces = c.getInterfaces();
                for (Class<?> inter : interfaces) {
                    instanceMap.put(inter.getName(), o);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        });
    }

    private void doDi() {
        instanceMap.forEach((k, v) -> {
            Object o = v;
            //注入实体对象
            Object i = null;
            Class<?> c = v.getClass();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    String name = field.getType().getName();
                    i = this.instanceMap.get(name);
                    field.setAccessible(true);
                    try {
                        field.set(o, i);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void doAutoInject() {
//        doInstance();
//        classList.forEach(this::doInject);
        classList.forEach(this::injectInstance);
    }

    private void doInject(Class<?> c) {
        String key = c.getSimpleName().substring(0, 1).toLowerCase() + c.getSimpleName().substring(1);
        if (!instanceMap.containsKey(key)) {
            return;
        }
        Object o = instanceMap.get(key);
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyAutowired.class)) {
                doInject(field.getType());
                String name = field.getType().getName();
                Object i = this.instanceMap.get(name);
                field.setAccessible(true);
                try {
                    field.set(o, i);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void injectInstance(Class<?> c) {
        Optional<Class<?>> optionalClass = classList.stream().filter(c::isAssignableFrom).findFirst();
        if (!optionalClass.isPresent()) {
            throw new RuntimeException("Can not inject class");
        }
        Class<?> c1 = optionalClass.get();
        String key = c1.getSimpleName().substring(0, 1).toLowerCase() + c1.getSimpleName().substring(1);
        if (instanceMap.containsKey(key)) {
            return;//已经实例化过，避免循环依赖导致死循环
        }
        try {
            Object o = c1.newInstance();
            instanceMap.put(key, o);
            Class<?>[] interfaces = c1.getInterfaces();
            for (Class<?> inter : interfaces) {
                instanceMap.put(inter.getName(), o);
            }
            Field[] fields = c1.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    injectInstance(field.getType());
                    String beanName;
                    if(field.getType().isInterface()){
                        beanName = field.getType().getName();
                    }else {
                        String name = field.getType().getSimpleName();
                        beanName = name.substring(0, 1).toLowerCase() + name.substring(1);
                    }

                    Object i = this.instanceMap.get(beanName);
                    field.setAccessible(true);
                    try {
                        field.set(o, i);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    public Object getBean(String beanName) {
        return instanceMap.get(beanName);
    }

    public static void main(String[] args) {
        MyApplicationConext conext = new MyApplicationConext("com.example.demo");
        UserBean user = (UserBean) conext.getBean("userBean");
        if (user != null) {
            user.getUser();
        }
        MyService service = (MyService) conext.getBean("myService");
        if (service != null) {
            service.print();
        }
    }
}
